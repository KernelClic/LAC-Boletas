#!/bin/bash

# ==============================================================================
# Script para probar la API REST ORDS de Boletas (Pruebas en caliente)
# ==============================================================================

# Configuración
ORDS_BASE_URL="https://poco.absapex.net/apex/api_boletas/api_boletas/v1"
AUTH_USER="usuario_ords"
AUTH_PASS="clave_ords"
ID_SORTEO=9999
TOKEN_PRUEBA="token-hot-test-$(date +%s)"

echo "====================================================="
echo "INICIANDO PRUEBAS EN CALIENTE APEX/ORDS BOLETAS"
echo "URL BASE: $ORDS_BASE_URL"
echo "TOKEN DE PRUEBA: $TOKEN_PRUEBA"
echo "====================================================="

# 1. Prueba de Sincronización de Lote (POST /boletas/batch)
echo -e "\n[-->] 1. Probando POST $ORDS_BASE_URL/boletas/batch"

PAYLOAD='{
  "id_sorteo": '$ID_SORTEO',
  "boletas": [
    {
      "numero": "100",
      "qr_token": "'$TOKEN_PRUEBA'"
    }
  ]
}'

echo "Enviando Payload:"
echo $PAYLOAD | jq . 2>/dev/null || echo $PAYLOAD

echo -e "\nRespuesta del servidor:"
curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X POST "$ORDS_BASE_URL/boletas/batch" \
  -H "Content-Type: application/json" \
  -u "$AUTH_USER:$AUTH_PASS" \
  -d "$PAYLOAD"

# 2. Prueba de Consulta (GET /qr/consultar/:token)
echo -e "\n-----------------------------------------------------"
echo "[-->] 2. Probando GET $ORDS_BASE_URL/qr/consultar/$TOKEN_PRUEBA"

echo -e "\nRespuesta del servidor:"
curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X GET "$ORDS_BASE_URL/qr/consultar/$TOKEN_PRUEBA"

# 3. Prueba de Consulta Ruta Original APP (GET /qr/consultar/?t=token)
echo -e "\n-----------------------------------------------------"
echo "[-->] 3. Probando GET APP ORIGINAL $ORDS_BASE_URL/qr/consultar/?t=$TOKEN_PRUEBA"

echo -e "\nRespuesta del servidor:"
curl -s -w "\nHTTP_STATUS:%{http_code}\n" -X GET "$ORDS_BASE_URL/qr/consultar/?t=$TOKEN_PRUEBA"

echo -e "\n====================================================="
echo "PRUEBAS FINALIZADAS"
echo "====================================================="
