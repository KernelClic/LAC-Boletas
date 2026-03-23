-- =============================================================================
-- Migración: Agregar PRIORIDAD y constraint único a CNL_RANGOS_PREMIO
-- Ejecutar en Oracle APEX SQL Workshop sobre la BD de producción.
-- =============================================================================

-- 1. Agregar columna PRIORIDAD (si no existe)
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_tab_columns
    WHERE table_name = 'CNL_RANGOS_PREMIO' AND column_name = 'PRIORIDAD';

    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE CNL_RANGOS_PREMIO ADD (PRIORIDAD NUMBER DEFAULT 1 NOT NULL)';
        DBMS_OUTPUT.PUT_LINE('OK: Columna PRIORIDAD agregada a CNL_RANGOS_PREMIO.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('OK: Columna PRIORIDAD ya existe.');
    END IF;
END;
/

-- 2. Rellenar PRIORIDAD = 1 en filas existentes que tengan NULL
UPDATE CNL_RANGOS_PREMIO SET PRIORIDAD = 1 WHERE PRIORIDAD IS NULL;
COMMIT;

-- 3. Agregar constraint UNIQUE (ID_SORTEO, RANGO_INICIAL, RANGO_FINAL) — evita duplicados
DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM user_constraints
    WHERE table_name = 'CNL_RANGOS_PREMIO' AND constraint_name = 'UK_RANGO_NUMEROS';

    IF v_count = 0 THEN
        -- Primero eliminar posibles duplicados existentes (conserva el de mayor ID_RANGO)
        DELETE FROM CNL_RANGOS_PREMIO a
        WHERE a.ROWID > (
            SELECT MIN(b.ROWID)
            FROM CNL_RANGOS_PREMIO b
            WHERE b.ID_SORTEO     = a.ID_SORTEO
              AND b.RANGO_INICIAL = a.RANGO_INICIAL
              AND b.RANGO_FINAL   = a.RANGO_FINAL
        );
        DBMS_OUTPUT.PUT_LINE('Duplicados eliminados: ' || SQL%ROWCOUNT || ' fila(s).');
        COMMIT;

        EXECUTE IMMEDIATE 'ALTER TABLE CNL_RANGOS_PREMIO ADD CONSTRAINT UK_RANGO_NUMEROS UNIQUE (ID_SORTEO, RANGO_INICIAL, RANGO_FINAL)';
        DBMS_OUTPUT.PUT_LINE('OK: Constraint UK_RANGO_NUMEROS creado.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('OK: Constraint UK_RANGO_NUMEROS ya existe.');
    END IF;
END;
/

-- 4. Verificar resultado final
SELECT column_name, data_type, data_default, nullable
FROM user_tab_columns
WHERE table_name = 'CNL_RANGOS_PREMIO'
ORDER BY column_id;

SELECT constraint_name, constraint_type, status
FROM user_constraints
WHERE table_name = 'CNL_RANGOS_PREMIO';
