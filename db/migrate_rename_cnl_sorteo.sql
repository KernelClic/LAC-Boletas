-- =============================================================================
-- Migración: Renombrar CNL_SORTEO → CNL_SORTEOS
-- Ejecutar en Oracle APEX si la tabla fue creada con el nombre incorrecto.
-- =============================================================================

-- 1. Renombrar la tabla
DECLARE
    v_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_exists
    FROM user_tables
    WHERE table_name = 'CNL_SORTEO';

    IF v_exists > 0 THEN
        EXECUTE IMMEDIATE 'RENAME CNL_SORTEO TO CNL_SORTEOS';
        DBMS_OUTPUT.PUT_LINE('Tabla CNL_SORTEO renombrada a CNL_SORTEOS correctamente.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Tabla CNL_SORTEO no existe — nada que renombrar.');
    END IF;
END;
/

-- 2. Verificar que CNL_SORTEOS exista tras la migración
DECLARE
    v_exists NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_exists
    FROM user_tables
    WHERE table_name = 'CNL_SORTEOS';

    IF v_exists > 0 THEN
        DBMS_OUTPUT.PUT_LINE('OK: Tabla CNL_SORTEOS existe.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('ERROR: Tabla CNL_SORTEOS NO encontrada. Revisa el esquema.');
    END IF;
END;
/

-- 3. Agregar columnas faltantes en TRP_BOLETAS (si la tabla ya existía antes de la migración)
-- Requerido para que PKG_BOLETAS_API compile sin ORA-00904
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tab_columns
    WHERE table_name = 'TRP_BOLETAS' AND column_name = 'NUMEROS_OPORTUNIDADES';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE TRP_BOLETAS ADD (NUMEROS_OPORTUNIDADES VARCHAR2(500 BYTE))';
        DBMS_OUTPUT.PUT_LINE('OK: Columna NUMEROS_OPORTUNIDADES agregada a TRP_BOLETAS.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('OK: Columna NUMEROS_OPORTUNIDADES ya existe en TRP_BOLETAS.');
    END IF;
END;
/

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tab_columns
    WHERE table_name = 'TRP_BOLETAS' AND column_name = 'LOTE_ORIGEN';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE TRP_BOLETAS ADD (LOTE_ORIGEN VARCHAR2(50 BYTE))';
        DBMS_OUTPUT.PUT_LINE('OK: Columna LOTE_ORIGEN agregada a TRP_BOLETAS.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('OK: Columna LOTE_ORIGEN ya existe en TRP_BOLETAS.');
    END IF;
END;
/

-- 4. Verificar que las FKs dependientes apunten correctamente
--    (Oracle renombra en cascada las FK constraints automáticamente)
SELECT constraint_name, table_name, r_constraint_name
FROM user_constraints
WHERE constraint_type = 'R'
  AND r_constraint_name IN (
      SELECT constraint_name FROM user_constraints
      WHERE table_name = 'CNL_SORTEOS' AND constraint_type = 'P'
  );

COMMIT;
