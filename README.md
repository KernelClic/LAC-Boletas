# LAC_Boletas

## Configuracion local de sincronizacion ORDS

El sincronizador busca por defecto el archivo `config/boletas-sync.properties`.

- `config/boletas-sync.properties` es local y esta ignorado por Git.
- `config/boletas-sync.example.properties` si se versiona y sirve como plantilla.
- Si necesita otra ruta, puede arrancar la aplicacion con `-Dboletas.sync.config.path=/ruta/archivo.properties`.
