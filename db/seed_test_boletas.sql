-- =============================================================================
-- Script Seed para Pruebas en Caliente
-- Inyecta los datos foráneos requeridos por la DB para no fallar el FK y
-- define el rango de premios para que el POST y el GET sean 100% exitosos.
-- =============================================================================

-- PRECAUCIÓN: Asegúrate de que los nombres de las tablas maestras coincidan 
-- con los de tu esquema real en APEX (por ejemplo cnl_sorteos).

-- 1. Insertar Sorteo Maestro 9999 para satisfacer la ForeignKey (SYS_C0025546)
BEGIN
    INSERT INTO cnl_sorteos (id_sorteo, nombre_sorteo, estado) 
    VALUES (9999, 'Sorteo Test Automatizado', 'ACTIVO');
    DBMS_OUTPUT.PUT_LINE('Sorteo 9999 insertado.');
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN 
        DBMS_OUTPUT.PUT_LINE('Sorteo 9999 ya existía.');
    WHEN OTHERS THEN
        -- La tabla correcta es CNL_SORTEOS (con 's'). Verifica que no exista CNL_SORTEO sin 's'.
        DBMS_OUTPUT.PUT_LINE('Error insertando sorteo (verifica el nombre de tu tabla de sorteos): ' || SQLERRM);
END;
/

-- 2. Insertar Rango de Premios para la Boleta 100
-- Requerido para probar el escenario de éxito en el paquete PKG_BOLETAS_API.consultar_premio_qr
BEGIN
    INSERT INTO cnl_rangos_premio (id_rango, id_sorteo, estado, rango_inicial, rango_final, mensaje_premio)
    VALUES (99999, 9999, 'ACTIVO', 100, 100, '¡Felicidades! Has ganado el premio de prueba en caliente.');
    DBMS_OUTPUT.PUT_LINE('Rango de premio insertado.');
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN 
        -- Actualizamos el mensaje si ya existe
        UPDATE cnl_rangos_premio 
           SET estado = 'ACTIVO', mensaje_premio = '¡Felicidades! Has ganado el premio de prueba en caliente.'
         WHERE id_rango = 99999;
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Error en cnl_rangos_premio: ' || SQLERRM);
END;
/

COMMIT;
