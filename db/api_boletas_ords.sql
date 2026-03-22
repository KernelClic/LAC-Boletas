-- =============================================================================
-- Script de Instalación: API Boletas para Oracle APEX / ORDS
-- Descripción: Crea el paquete PKG_BOLETAS_API y expone los servicios REST
--              necesarios para la sincronización de boletas desde la app Java.
-- =============================================================================

-- 1. PAQUETE PKG_BOLETAS_API (Especificación)
CREATE OR REPLACE PACKAGE PKG_BOLETAS_API AS
    -- Procedimiento para el Batch Insert del Cliente (Llamado via POST /batch)
    PROCEDURE sync_lote_boletas(p_payload IN CLOB);
    -- Procedimiento resolutor del escaner (Llamado via GET /qr/:token)
    PROCEDURE consultar_premio_qr(p_qr_token IN VARCHAR2);
END PKG_BOLETAS_API;
/

-- 2. PAQUETE PKG_BOLETAS_API (Cuerpo)
CREATE OR REPLACE PACKAGE BODY PKG_BOLETAS_API AS

    PROCEDURE sync_lote_boletas(p_payload IN CLOB) IS
        v_id_sorteo NUMBER;
    BEGIN
        -- Desglose del JSON de Payload
        -- Asume un formato: {"id_sorteo": 105, "boletas": [{"numero": "1", "qr_token": "abc"}]}

        -- Extraer cabecera
        SELECT JSON_VALUE(p_payload, '$.id_sorteo') INTO v_id_sorteo FROM DUAL;

        -- Insert_All masivo extrayendolo todo de la Array JSON
        INSERT INTO trp_boletas (id_sorteo, numero_boleta, qr_token, estado_redencion)
        SELECT v_id_sorteo, jt.numero, jt.qr_token, 'NO_CONSULTADA'
        FROM JSON_TABLE(
            p_payload,
            '$.boletas[*]'
            COLUMNS (
                numero VARCHAR2(20) PATH '$.numero',
                qr_token VARCHAR2(100) PATH '$.qr_token'
            )
        ) jt;

        -- Se genera un HTTP 200/201 implicitamente si no hay excepcioes Oracle
        -- Si hay colisión Unique de qr_token fallará con http 500 y hará rollback automatico
    END sync_lote_boletas;


    PROCEDURE consultar_premio_qr(p_qr_token IN VARCHAR2) IS
        v_boleta_id trp_boletas.id_boleta%TYPE;
        v_num_boleta trp_boletas.numero_boleta%TYPE; 
        v_sorteo_id trp_boletas.id_sorteo%TYPE;
        v_estado trp_boletas.estado_redencion%TYPE;
        v_lote trp_boletas.lote_origen%TYPE;

        v_mensaje_premio VARCHAR2(500) := 'Sigue intentando... Esta vez no hubo suerte.'; 
        v_tiene_premio BOOLEAN := FALSE;
    BEGIN
        -- Paso 1. Verificar Autenticidad del UUID Token
        SELECT id_boleta, numero_boleta, id_sorteo, estado_redencion
        INTO v_boleta_id, v_num_boleta, v_sorteo_id, v_estado 
        FROM trp_boletas WHERE qr_token = p_qr_token;

        -- Paso 2. Impedir fraude de 'Doble Gasto' / Re-canje
        IF v_estado = 'CONSULTADA' THEN
            -- HTP se encarga de imprimir la respuesta al cliente
            HTP.p('{"estado":"YA_CONSULTADA", "mensaje":"Esta boleta ya fue redimida. Para dudas comunícate con soporte."}');
            RETURN;
        END IF;

        -- Paso 3. Comprobar si cruza con un rango de premios
        BEGIN
            SELECT mensaje_premio INTO v_mensaje_premio 
            FROM cnl_rangos_premio rp 
            WHERE rp.id_sorteo = v_sorteo_id 
              AND rp.estado = 'ACTIVO' 
              AND TO_NUMBER(v_num_boleta) BETWEEN rp.rango_inicial AND rp.rango_final;

            v_tiene_premio := TRUE;
        EXCEPTION 
            WHEN NO_DATA_FOUND THEN NULL; -- Sigue sin premio, valor Default
        END;

        -- Paso 4. Desquemar boleta y Auditoría Legal
        UPDATE trp_boletas SET estado_redencion = 'CONSULTADA' WHERE id_boleta = v_boleta_id;

        INSERT INTO trd_consultas_qr (id_boleta, ip_dispositivo, agente_usuario, mensaje_mostrado) 
        VALUES (
            v_boleta_id, 
            COALESCE(OWA_UTIL.get_cgi_env('HTTP_X_FORWARDED_FOR'), OWA_UTIL.get_cgi_env('REMOTE_ADDR'), 'IP-UNKN'), 
            OWA_UTIL.get_cgi_env('HTTP_USER_AGENT'),
            v_mensaje_premio
        );

        -- Respuesta HTTP
        IF v_tiene_premio THEN
            HTP.p('{"estado":"PREMIO", "mensaje":"' || v_mensaje_premio || '"}');
        ELSE
            HTP.p('{"estado":"SIN_PREMIO", "mensaje":"' || v_mensaje_premio || '"}');
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN 
            -- Auditoria de Escaneos de Boletas Truchas o Scrappers
            INSERT INTO aud_intentos_qr (qr_token_intentado, ip_origen, motivo_rechazo) 
            VALUES (
                p_qr_token, 
                COALESCE(OWA_UTIL.get_cgi_env('HTTP_X_FORWARDED_FOR'), OWA_UTIL.get_cgi_env('REMOTE_ADDR'), 'IP-UNKN'), 
                'Boleta Inexistente. QR Adulterado o Sorteo inexistente.'
            );

            -- Modificamos explícitamente el STATUS de ORDS si está configurado el implicit bind o fallamos gracefully
            -- :status_code := 404; 
            HTP.p('{"estado":"ERROR", "mensaje":"La boleta escaneada no existe en los registros oficiales."}');
    END consultar_premio_qr;

END PKG_BOLETAS_API;
/

-- 3. DEFINICIÓN DEL MÓDULO ORDS
BEGIN
  -- Eliminar modulo previo si existe para recrearlo
  ORDS.DELETE_MODULE(
      p_module_name => 'api_boletas'
  );

  -- Crear módulo v1
  ORDS.DEFINE_MODULE(
      p_module_name    => 'api_boletas',
      p_base_path      => '/api_boletas/v1/',
      p_items_per_page => 25,
      p_status         => 'PUBLISHED',
      p_comments       => 'API de sincronización e integración de Boletas'
  );

  -- =========================================================================
  -- Endpoint: POST /boletas/batch
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'boletas/batch',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Recibe un lote de boletas para sincronizar en la nube'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'boletas/batch',
      p_method         => 'POST',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => 'application/json',
      p_comments       => 'Llama al procedure de inserción pasándole el body completo',
      p_source         => 'BEGIN
                             -- ORDS mapea automáticamente el body de la petición al bind :body_text (CLOB)
                             PKG_BOLETAS_API.sync_lote_boletas(p_payload => :body_text);
                             
                             -- Devolver una respuesta JSON válida requerida por el cliente Java (recibimos N)
                             OWA_UTIL.status_line(201, ''Created'', FALSE);
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             HTP.p(''{"status":"registered"}'');
                           END;'
  );

  -- =========================================================================
  -- Endpoint: GET /qr/consultar/:token
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'qr/consultar/:token',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Consulta el estado de una boleta mediante su token QR'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'qr/consultar/:token',
      p_method         => 'GET',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => '',
      p_comments       => 'Devuelve la información procesada usando HTP.p desde el procedimiento',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.consultar_premio_qr(p_qr_token => :token);
                           END;'
  );

  COMMIT;
END;
/
