-- =============================================================================
-- Script de Instalación: API Boletas para Oracle APEX / ORDS
-- Descripción: Crea el paquete PKG_BOLETAS_API y expone los servicios REST
--              necesarios para la sincronización de boletas desde la app Java.
-- Incluye: validación de plazas, gestión de sorteos, rangos de premio,
--          y sincronización batch de boletas.
-- =============================================================================

-- 1. PAQUETE PKG_BOLETAS_API (Especificación)
CREATE OR REPLACE PACKAGE PKG_BOLETAS_API AS
    -- Batch Insert de boletas (POST /batch)
    PROCEDURE sync_lote_boletas(p_payload IN CLOB);
    -- Consulta de premio por QR (GET /qr/:token)
    PROCEDURE consultar_premio_qr(p_qr_token IN VARCHAR2);
    -- Validar plaza por código seguro (GET /plaza/validar/:codigo)
    PROCEDURE validar_plaza(p_codigo_seguro IN VARCHAR2);
    -- Crear nuevo sorteo para una plaza (POST /sorteo/crear)
    PROCEDURE crear_sorteo(p_payload IN CLOB);
    -- Obtener último sorteo de una plaza (GET /sorteo/ultimo/:id_plaza)
    PROCEDURE obtener_ultimo_sorteo(p_id_plaza IN NUMBER);
    -- Registrar rangos de premio (POST /rangos/registrar)
    PROCEDURE registrar_rangos(p_payload IN CLOB);
END PKG_BOLETAS_API;
/

-- 2. PAQUETE PKG_BOLETAS_API (Cuerpo)
CREATE OR REPLACE PACKAGE BODY PKG_BOLETAS_API AS

    -- =========================================================================
    -- SYNC_LOTE_BOLETAS: Recibe un lote JSON de boletas y las inserta
    -- Payload: {"id_sorteo": N, "boletas": [{"numero":"XXXX","qr_token":"uuid"}]}
    -- =========================================================================
    PROCEDURE sync_lote_boletas(p_payload IN CLOB) IS
        v_id_sorteo NUMBER;
    BEGIN
        SELECT JSON_VALUE(p_payload, '$.id_sorteo') INTO v_id_sorteo FROM DUAL;

        INSERT INTO trp_boletas (id_sorteo, numero_boleta, numeros_oportunidades, qr_token, estado_redencion)
        SELECT v_id_sorteo, jt.numero, jt.numeros_oportunidades, jt.qr_token, 'NO_CONSULTADA'
        FROM JSON_TABLE(
            p_payload,
            '$.boletas[*]'
            COLUMNS (
                numero VARCHAR2(20) PATH '$.numero',
                numeros_oportunidades VARCHAR2(500) PATH '$.numeros_oportunidades',
                qr_token VARCHAR2(100) PATH '$.qr_token'
            )
        ) jt;
    END sync_lote_boletas;

    -- =========================================================================
    -- CONSULTAR_PREMIO_QR: Verifica autenticidad del QR y devuelve resultado
    -- =========================================================================
    PROCEDURE consultar_premio_qr(p_qr_token IN VARCHAR2) IS
        v_boleta_id trp_boletas.id_boleta%TYPE;
        v_num_boleta trp_boletas.numero_boleta%TYPE; 
        v_sorteo_id trp_boletas.id_sorteo%TYPE;
        v_estado trp_boletas.estado_redencion%TYPE;

        v_mensaje_premio VARCHAR2(500) := 'Sigue intentando... Esta vez no hubo suerte.'; 
        v_tiene_premio BOOLEAN := FALSE;
    BEGIN
        -- Paso 1. Verificar Autenticidad del UUID Token
        SELECT id_boleta, numero_boleta, id_sorteo, estado_redencion
        INTO v_boleta_id, v_num_boleta, v_sorteo_id, v_estado 
        FROM trp_boletas WHERE qr_token = p_qr_token;

        -- Paso 2. Impedir fraude de doble consulta
        IF v_estado = 'CONSULTADA' THEN
            HTP.p('{"estado":"YA_CONSULTADA", "mensaje":"Esta boleta ya fue redimida. Para dudas comunícate con soporte."}');
            RETURN;
        END IF;

        -- Paso 3. Comprobar si cruza con un rango de premios.
        --         Si la boleta cae en varios rangos, se elige el de mayor PRIORIDAD (mayor número).
        BEGIN
            SELECT mensaje_premio INTO v_mensaje_premio
            FROM (
                SELECT mensaje_premio
                FROM cnl_rangos_premio rp
                WHERE rp.id_sorteo = v_sorteo_id
                  AND rp.estado = 'ACTIVO'
                  AND TO_NUMBER(v_num_boleta) BETWEEN rp.rango_inicial AND rp.rango_final
                ORDER BY NVL(rp.prioridad, 1) DESC
            ) WHERE ROWNUM = 1;

            v_tiene_premio := TRUE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN NULL;
        END;

        -- Paso 4. Marcar boleta como consultada
        UPDATE trp_boletas SET estado_redencion = 'CONSULTADA' WHERE id_boleta = v_boleta_id;

        INSERT INTO trd_consultas_qr (id_boleta, ip_dispositivo, agente_usuario, mensaje_mostrado) 
        VALUES (
            v_boleta_id, 
            COALESCE(OWA_UTIL.get_cgi_env('HTTP_X_FORWARDED_FOR'), OWA_UTIL.get_cgi_env('REMOTE_ADDR'), 'IP-UNKN'), 
            OWA_UTIL.get_cgi_env('HTTP_USER_AGENT'),
            v_mensaje_premio
        );

        IF v_tiene_premio THEN
            HTP.p('{"estado":"PREMIO", "mensaje":"' || v_mensaje_premio || '"}');
        ELSE
            HTP.p('{"estado":"SIN_PREMIO", "mensaje":"' || v_mensaje_premio || '"}');
        END IF;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN 
            INSERT INTO aud_intentos_qr (qr_token_intentado, ip_origen, motivo_rechazo) 
            VALUES (
                p_qr_token, 
                COALESCE(OWA_UTIL.get_cgi_env('HTTP_X_FORWARDED_FOR'), OWA_UTIL.get_cgi_env('REMOTE_ADDR'), 'IP-UNKN'), 
                'Boleta Inexistente. QR Adulterado o Sorteo inexistente.'
            );
            HTP.p('{"estado":"ERROR", "mensaje":"La boleta escaneada no existe en los registros oficiales."}');
    END consultar_premio_qr;

    -- =========================================================================
    -- VALIDAR_PLAZA: Verifica que un codigo_seguro exista y esté activo
    -- Retorna JSON: {id_plaza, nombre, estado} o error
    -- =========================================================================
    PROCEDURE validar_plaza(p_codigo_seguro IN VARCHAR2) IS
        v_id_plaza   CNL_PLAZAS.ID_PLAZA%TYPE;
        v_nombre     CNL_PLAZAS.NOMBRE%TYPE;
        v_estado     CNL_PLAZAS.ESTADO%TYPE;
    BEGIN
        SELECT ID_PLAZA, NOMBRE, ESTADO
        INTO v_id_plaza, v_nombre, v_estado
        FROM CNL_PLAZAS
        WHERE CODIGO_SEGURO = p_codigo_seguro;

        IF v_estado != 'ACTIVO' THEN
            HTP.p('{"estado":"INACTIVA", "mensaje":"La plaza existe pero está inactiva."}');
            RETURN;
        END IF;

        HTP.p('{"estado":"OK", "id_plaza":' || v_id_plaza 
            || ', "nombre":"' || v_nombre 
            || '", "estado_plaza":"' || v_estado || '"}');

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            HTP.p('{"estado":"ERROR", "mensaje":"Código de plaza no encontrado."}');
    END validar_plaza;

    -- =========================================================================
    -- CREAR_SORTEO: Crea un nuevo sorteo para la plaza especificada
    -- Payload: {"id_plaza": N, "nombre_sorteo": "...", "fecha_inicio": "YYYY-MM-DD", "fecha_fin": "YYYY-MM-DD"}
    -- Retorna: {id_sorteo, nombre_sorteo, estado}
    -- =========================================================================
    PROCEDURE crear_sorteo(p_payload IN CLOB) IS
        v_id_plaza      NUMBER;
        v_nombre_sorteo VARCHAR2(150);
        v_fecha_inicio  VARCHAR2(20);
        v_fecha_fin     VARCHAR2(20);
        v_id_sorteo     NUMBER;
        v_plaza_estado  VARCHAR2(20);
    BEGIN
        v_id_plaza      := JSON_VALUE(p_payload, '$.id_plaza' RETURNING NUMBER);
        v_nombre_sorteo := JSON_VALUE(p_payload, '$.nombre_sorteo');
        v_fecha_inicio  := JSON_VALUE(p_payload, '$.fecha_inicio');
        v_fecha_fin     := JSON_VALUE(p_payload, '$.fecha_fin');

        -- Verificar que la plaza exista y esté activa
        BEGIN
            SELECT ESTADO INTO v_plaza_estado 
            FROM CNL_PLAZAS WHERE ID_PLAZA = v_id_plaza;
            
            IF v_plaza_estado != 'ACTIVO' THEN
                HTP.p('{"estado":"ERROR", "mensaje":"La plaza no está activa."}');
                RETURN;
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                HTP.p('{"estado":"ERROR", "mensaje":"Plaza no encontrada."}');
                RETURN;
        END;

        INSERT INTO CNL_SORTEOS (ID_PLAZA, NOMBRE_SORTEO, FECHA_INICIO, FECHA_FIN, ESTADO)
        VALUES (
            v_id_plaza, 
            v_nombre_sorteo,
            CASE WHEN v_fecha_inicio IS NOT NULL THEN TO_DATE(v_fecha_inicio, 'YYYY-MM-DD') ELSE SYSDATE END,
            CASE WHEN v_fecha_fin IS NOT NULL THEN TO_DATE(v_fecha_fin, 'YYYY-MM-DD') ELSE NULL END,
            'ACTIVO'
        )
        RETURNING ID_SORTEO INTO v_id_sorteo;

        COMMIT;

        HTP.p('{"estado":"OK", "id_sorteo":' || v_id_sorteo 
            || ', "nombre_sorteo":"' || v_nombre_sorteo 
            || '", "id_plaza":' || v_id_plaza || '}');

    END crear_sorteo;

    -- =========================================================================
    -- OBTENER_ULTIMO_SORTEO: Retorna el último sorteo de una plaza
    -- =========================================================================
    PROCEDURE obtener_ultimo_sorteo(p_id_plaza IN NUMBER) IS
        v_id_sorteo     NUMBER;
        v_nombre_sorteo VARCHAR2(150);
        v_estado        VARCHAR2(20);
    BEGIN
        SELECT ID_SORTEO, NOMBRE_SORTEO, ESTADO
        INTO v_id_sorteo, v_nombre_sorteo, v_estado
        FROM (
            SELECT ID_SORTEO, NOMBRE_SORTEO, ESTADO
            FROM CNL_SORTEOS
            WHERE ID_PLAZA = p_id_plaza
            ORDER BY ID_SORTEO DESC
        )
        WHERE ROWNUM = 1;

        HTP.p('{"estado":"OK", "id_sorteo":' || v_id_sorteo 
            || ', "nombre_sorteo":"' || v_nombre_sorteo 
            || '", "estado_sorteo":"' || v_estado || '"}');

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            HTP.p('{"estado":"SIN_SORTEOS", "mensaje":"No hay sorteos registrados para esta plaza.", "id_sorteo":0}');
    END obtener_ultimo_sorteo;

    -- =========================================================================
    -- REGISTRAR_RANGOS: Registra rangos de premio para un sorteo.
    -- Payload: {"id_sorteo": N, "rangos": [{"rango_inicial":1,"rango_final":100,
    --           "mensaje_premio":"...","prioridad":1,"fecha_vigencia":"YYYY-MM-DD"}]}
    -- Prioridad: número mayor = premio de mayor jerarquía cuando una boleta cae en
    --            varios rangos simultáneamente.
    -- Duplicados: rangos con igual (id_sorteo, rango_inicial, rango_final) son ignorados.
    -- =========================================================================
    PROCEDURE registrar_rangos(p_payload IN CLOB) IS
        v_id_sorteo NUMBER;
        v_inserted  NUMBER := 0;
        v_skipped   NUMBER := 0;
    BEGIN
        v_id_sorteo := JSON_VALUE(p_payload, '$.id_sorteo' RETURNING NUMBER);

        -- Verificar que el sorteo exista
        DECLARE
            v_check NUMBER;
        BEGIN
            SELECT 1 INTO v_check FROM CNL_SORTEOS WHERE ID_SORTEO = v_id_sorteo;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                HTP.p('{"estado":"ERROR", "mensaje":"Sorteo no encontrado."}');
                RETURN;
        END;

        -- MERGE: inserta rangos nuevos, ignora duplicados (mismo sorteo + rango_inicial + rango_final)
        MERGE INTO CNL_RANGOS_PREMIO tgt
        USING (
            SELECT v_id_sorteo                                                       AS id_sorteo,
                   jt.rango_inicial,
                   jt.rango_final,
                   jt.mensaje_premio,
                   NVL(jt.prioridad, 1)                                             AS prioridad,
                   CASE WHEN jt.fecha_vigencia IS NOT NULL
                        THEN TO_DATE(jt.fecha_vigencia, 'YYYY-MM-DD')
                        ELSE NULL END                                                AS fecha_vig
            FROM JSON_TABLE(
                p_payload,
                '$.rangos[*]'
                COLUMNS (
                    rango_inicial  NUMBER       PATH '$.rango_inicial',
                    rango_final    NUMBER       PATH '$.rango_final',
                    mensaje_premio VARCHAR2(500) PATH '$.mensaje_premio',
                    prioridad      NUMBER       PATH '$.prioridad',
                    fecha_vigencia VARCHAR2(20)  PATH '$.fecha_vigencia'
                )
            ) jt
        ) src
        ON (    tgt.ID_SORTEO     = src.id_sorteo
            AND tgt.RANGO_INICIAL = src.rango_inicial
            AND tgt.RANGO_FINAL   = src.rango_final)
        WHEN NOT MATCHED THEN
            INSERT (ID_SORTEO, RANGO_INICIAL, RANGO_FINAL, MENSAJE_PREMIO,
                    PRIORIDAD, ESTADO, FECHA_VIGENCIA)
            VALUES (src.id_sorteo, src.rango_inicial, src.rango_final, src.mensaje_premio,
                    src.prioridad, 'ACTIVO', src.fecha_vig);

        v_inserted := SQL%ROWCOUNT;

        -- Calcular cuántos venían en el payload para determinar los omitidos
        SELECT COUNT(*) INTO v_skipped
        FROM JSON_TABLE(p_payload, '$.rangos[*]' COLUMNS (dummy VARCHAR2(1) PATH '$'))
        WHERE 1=1;
        v_skipped := v_skipped - v_inserted;

        COMMIT;

        HTP.p('{"estado":"OK", "rangos_registrados":' || v_inserted
            || ', "rangos_omitidos_duplicados":' || v_skipped
            || ', "id_sorteo":' || v_id_sorteo || '}');

    END registrar_rangos;

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
      p_comments       => 'API de sincronización e integración de Boletas con Plazas y Sorteos'
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
                             PKG_BOLETAS_API.sync_lote_boletas(p_payload => :body_text);
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
      p_comments       => 'Devuelve la información procesada usando HTP.p',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.consultar_premio_qr(p_qr_token => :token);
                           END;'
  );

  -- =========================================================================
  -- Endpoint: GET /plaza/validar/:codigo_seguro
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'plaza/validar/:codigo_seguro',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Valida un código de plaza y retorna sus datos'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'plaza/validar/:codigo_seguro',
      p_method         => 'GET',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => '',
      p_comments       => 'Valida plaza por código seguro',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.validar_plaza(p_codigo_seguro => :codigo_seguro);
                           END;'
  );

  -- =========================================================================
  -- Endpoint: POST /sorteo/crear
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'sorteo/crear',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Crea un nuevo sorteo para una plaza'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'sorteo/crear',
      p_method         => 'POST',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => 'application/json',
      p_comments       => 'Crea sorteo para la plaza especificada en el body',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.crear_sorteo(p_payload => :body_text);
                           END;'
  );

  -- =========================================================================
  -- Endpoint: GET /sorteo/ultimo/:id_plaza
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'sorteo/ultimo/:id_plaza',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Obtiene el último sorteo de una plaza'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'sorteo/ultimo/:id_plaza',
      p_method         => 'GET',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => '',
      p_comments       => 'Retorna datos del último sorteo',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.obtener_ultimo_sorteo(p_id_plaza => TO_NUMBER(:id_plaza));
                           END;'
  );

  -- =========================================================================
  -- Endpoint: POST /rangos/registrar
  -- =========================================================================
  ORDS.DEFINE_TEMPLATE(
      p_module_name    => 'api_boletas',
      p_pattern        => 'rangos/registrar',
      p_priority       => 0,
      p_etag_type      => 'HASH',
      p_etag_query     => NULL,
      p_comments       => 'Registra rangos de premio para un sorteo'
  );

  ORDS.DEFINE_HANDLER(
      p_module_name    => 'api_boletas',
      p_pattern        => 'rangos/registrar',
      p_method         => 'POST',
      p_source_type    => 'plsql/block',
      p_mimes_allowed  => 'application/json',
      p_comments       => 'Registra los rangos de premio desde el body JSON',
      p_source         => 'BEGIN
                             OWA_UTIL.mime_header(''application/json'', TRUE);
                             PKG_BOLETAS_API.registrar_rangos(p_payload => :body_text);
                           END;'
  );

  COMMIT;
END;
/
