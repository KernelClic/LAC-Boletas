package Controlador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Servicio de integración Cloud para Plazas, Sorteos y Rangos de Premio.
 * 
 * Usa la configuración de BoletaCloudSync.Config para URLs,
 * autenticación y timeouts.
 */
public class PlazaCloudService {

    private final BoletaCloudSync.Config config;

    // URLs de los nuevos endpoints (leídas desde config)
    private final String urlPlazaValidar;
    private final String urlSorteoCrear;
    private final String urlSorteoUltimo;
    private final String urlRangosRegistrar;
    private final String urlSorteoDetalle;
    private final String urlBoletaEstado;
    private final String urlRangosLimpiar;

    /**
     * Resultado genérico de una llamada HTTP al cloud.
     */
    public static class CloudResult {
        public final boolean exito;
        public final String estado;
        public final String mensaje;
        public final JSONObject jsonCompleto;

        public CloudResult(boolean exito, String estado, String mensaje, JSONObject jsonCompleto) {
            this.exito = exito;
            this.estado = estado;
            this.mensaje = mensaje;
            this.jsonCompleto = jsonCompleto;
        }
    }

    /**
     * Datos de una plaza validada.
     */
    public static class PlazaInfo {
        public final int idPlaza;
        public final String nombre;
        public final String estado;

        public PlazaInfo(int idPlaza, String nombre, String estado) {
            this.idPlaza = idPlaza;
            this.nombre = nombre;
            this.estado = estado;
        }
    }

    /**
     * Datos de un rango de premio.
     * prioridad: número mayor = mayor jerarquía cuando una boleta cae en varios rangos.
     */
    public static class RangoPremio {
        public final int rangoInicial;
        public final int rangoFinal;
        public final String mensajePremio;
        public final String fechaVigencia; // formato YYYY-MM-DD o null
        public final int prioridad;        // >= 1; mayor número = mayor prioridad

        public RangoPremio(int rangoInicial, int rangoFinal, String mensajePremio, String fechaVigencia, int prioridad) {
            this.rangoInicial = rangoInicial;
            this.rangoFinal = rangoFinal;
            this.mensajePremio = mensajePremio;
            this.fechaVigencia = fechaVigencia;
            this.prioridad = prioridad > 0 ? prioridad : 1;
        }
    }

    /**
     * Datos de un sorteo con sus rangos de premio.
     */
    public static class SorteoDetalle {
        public final int idSorteo;
        public final String nombreSorteo;
        public final String estadoSorteo;
        public final java.util.List<RangoPremio> rangos;

        public SorteoDetalle(int idSorteo, String nombreSorteo, String estadoSorteo,
                             java.util.List<RangoPremio> rangos) {
            this.idSorteo     = idSorteo;
            this.nombreSorteo = nombreSorteo;
            this.estadoSorteo = estadoSorteo;
            this.rangos       = rangos;
        }
    }

    /**
     * Estado de redención de una boleta.
     */
    public static class EstadoBoleta {
        public final String numeroBoleta;
        public final String estadoRedencion;   // "NO_CONSULTADA" | "CONSULTADA"
        public final String fechaGeneracion;
        public final String fechaRedencion;    // null si no ha sido redimida
        public final String mensaje;

        public EstadoBoleta(String numeroBoleta, String estadoRedencion,
                            String fechaGeneracion, String fechaRedencion, String mensaje) {
            this.numeroBoleta   = numeroBoleta;
            this.estadoRedencion = estadoRedencion;
            this.fechaGeneracion = fechaGeneracion;
            this.fechaRedencion  = fechaRedencion;
            this.mensaje         = mensaje;
        }
    }

    public PlazaCloudService() {
        this(BoletaCloudSync.Config.fromSystem());
    }

    public PlazaCloudService(BoletaCloudSync.Config config) {
        this.config = config;

        // Derivar URLs de los endpoints a partir de la URL base del ORDS
        // URL base actual:
        // https://poco.absapex.net/apex/api_boletas/api_boletas/v1/boletas/batch
        // Extraemos hasta /v1/
        String baseUrl = config.ordsBatchUrl;
        int v1Index = baseUrl.indexOf("/v1/");
        String baseApiUrl;
        if (v1Index > 0) {
            baseApiUrl = baseUrl.substring(0, v1Index + 4); // incluye /v1/
        } else {
            // Fallback: usar URL directa
            baseApiUrl = "https://poco.absapex.net/apex/api_boletas/api_boletas/v1/";
        }

        this.urlPlazaValidar = baseApiUrl + "plaza/validar/";
        this.urlSorteoCrear = baseApiUrl + "sorteo/crear";
        this.urlSorteoUltimo = baseApiUrl + "sorteo/ultimo/";
        this.urlRangosRegistrar = baseApiUrl + "rangos/registrar";
        this.urlSorteoDetalle = baseApiUrl + "sorteo/detalle/";
        this.urlBoletaEstado  = baseApiUrl + "boleta/estado/";
        this.urlRangosLimpiar = baseApiUrl + "rangos/limpiar";

        System.out.println("[PlazaCloud] URLs configuradas:");
        System.out.println("[PlazaCloud]   plaza/validar -> " + urlPlazaValidar);
        System.out.println("[PlazaCloud]   sorteo/crear  -> " + urlSorteoCrear);
        System.out.println("[PlazaCloud]   sorteo/ultimo -> " + urlSorteoUltimo);
        System.out.println("[PlazaCloud]   rangos/reg    -> " + urlRangosRegistrar);
        System.out.println("[PlazaCloud]   sorteo/detalle -> " + urlSorteoDetalle);
        System.out.println("[PlazaCloud]   boleta/estado  -> " + urlBoletaEstado);
        System.out.println("[PlazaCloud]   rangos/limpiar -> " + urlRangosLimpiar);
    }

    // =========================================================================
    // VALIDAR PLAZA
    // =========================================================================
    /**
     * Valida un código de plaza contra el cloud.
     * 
     * @param codigoSeguro Código seguro de la plaza
     * @return PlazaInfo si la plaza es válida y activa, null si no
     */
    public PlazaInfo validarPlaza(String codigoSeguro) {
        if (codigoSeguro == null || codigoSeguro.trim().isEmpty()) {
            System.err.println("[PlazaCloud] Código de plaza vacío.");
            return null;
        }

        try {
            String url = urlPlazaValidar + codigoSeguro.trim();
            System.out.println("[PlazaCloud] Validando plaza: GET " + url);

            JSONObject resp = ejecutarGet(url);
            if (resp == null) {
                return null;
            }

            String estado = resp.optString("estado", "");
            if ("OK".equals(estado)) {
                PlazaInfo info = new PlazaInfo(
                        resp.getInt("id_plaza"),
                        resp.getString("nombre"),
                        resp.optString("estado_plaza", "ACTIVO"));
                System.out.println("[PlazaCloud] Plaza validada: id=" + info.idPlaza
                        + ", nombre=" + info.nombre);
                return info;
            } else {
                System.err.println("[PlazaCloud] Plaza no válida: " + resp.optString("mensaje", estado));
                return null;
            }
        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error validando plaza: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // =========================================================================
    // CREAR SORTEO
    // =========================================================================
    /**
     * Crea un nuevo sorteo para la plaza.
     * 
     * @param idPlaza      ID de plaza obtenido de validarPlaza()
     * @param nombreSorteo Nombre descriptivo del sorteo
     * @return ID del sorteo creado, o -1 si falla
     */
    public int crearSorteo(int idPlaza, String nombreSorteo) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_plaza", idPlaza);
            payload.put("nombre_sorteo", nombreSorteo);

            System.out.println("[PlazaCloud] Creando sorteo: POST " + urlSorteoCrear);
            System.out.println("[PlazaCloud]   id_plaza=" + idPlaza + ", nombre=" + nombreSorteo);

            JSONObject resp = ejecutarPost(urlSorteoCrear, payload.toString());
            if (resp == null) {
                return -1;
            }

            String estado = resp.optString("estado", "");
            if ("OK".equals(estado)) {
                int idSorteo = resp.getInt("id_sorteo");
                System.out.println("[PlazaCloud] Sorteo creado: id_sorteo=" + idSorteo);
                return idSorteo;
            } else {
                System.err.println("[PlazaCloud] Error creando sorteo: " + resp.optString("mensaje", estado));
                return -1;
            }
        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error creando sorteo: " + ex.getMessage());
            ex.printStackTrace();
            return -1;
        }
    }

    // =========================================================================
    // OBTENER ÚLTIMO SORTEO
    // =========================================================================
    /**
     * Obtiene el último sorteo registrado para la plaza.
     * 
     * @param idPlaza ID de la plaza
     * @return ID del último sorteo, o 0 si no hay sorteos
     */
    public int obtenerUltimoSorteo(int idPlaza) {
        try {
            String url = urlSorteoUltimo + idPlaza;
            System.out.println("[PlazaCloud] Consultando último sorteo: GET " + url);

            JSONObject resp = ejecutarGet(url);
            if (resp == null) {
                return 0;
            }

            String estado = resp.optString("estado", "");
            if ("OK".equals(estado)) {
                int idSorteo = resp.getInt("id_sorteo");
                System.out.println("[PlazaCloud] Último sorteo: id=" + idSorteo
                        + ", nombre=" + resp.optString("nombre_sorteo", ""));
                return idSorteo;
            } else {
                System.out.println("[PlazaCloud] " + resp.optString("mensaje", "Sin sorteos previos."));
                return 0;
            }
        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error consultando último sorteo: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    // =========================================================================
    // REGISTRAR RANGOS DE PREMIO
    // =========================================================================
    /**
     * Registra una lista de rangos de premio para un sorteo.
     * 
     * @param idSorteo ID del sorteo
     * @param rangos   Lista de rangos a registrar
     * @return CloudResult con el resultado
     */
    public CloudResult registrarRangos(int idSorteo, List<RangoPremio> rangos) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_sorteo", idSorteo);

            JSONArray rangosArray = new JSONArray();
            for (RangoPremio rango : rangos) {
                JSONObject r = new JSONObject();
                r.put("rango_inicial", rango.rangoInicial);
                r.put("rango_final", rango.rangoFinal);
                r.put("mensaje_premio", rango.mensajePremio);
                r.put("prioridad", rango.prioridad);
                if (rango.fechaVigencia != null && !rango.fechaVigencia.isEmpty()) {
                    r.put("fecha_vigencia", rango.fechaVigencia);
                }
                rangosArray.put(r);
            }
            payload.put("rangos", rangosArray);

            System.out.println("[PlazaCloud] Registrando " + rangos.size() + " rangos: POST " + urlRangosRegistrar);

            JSONObject resp = ejecutarPost(urlRangosRegistrar, payload.toString());
            if (resp == null) {
                return new CloudResult(false, "ERROR", "Sin respuesta del servidor.", null);
            }

            String estado = resp.optString("estado", "");
            if ("OK".equals(estado)) {
                int count = resp.optInt("rangos_registrados", 0);
                int skipped = resp.optInt("rangos_omitidos_duplicados", 0);
                System.out.println("[PlazaCloud] Rangos registrados: " + count + ", omitidos (duplicados): " + skipped);
                String msg = count + " rango(s) registrado(s) exitosamente.";
                if (skipped > 0) msg += " " + skipped + " omitido(s) por duplicado.";
                return new CloudResult(true, estado, msg, resp);
            } else {
                return new CloudResult(false, estado,
                        resp.optString("mensaje", "Error desconocido."), resp);
            }
        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error registrando rangos: " + ex.getMessage());
            ex.printStackTrace();
            return new CloudResult(false, "ERROR", ex.getMessage(), null);
        }
    }

    /**
     * Elimina (DELETE físico) todos los rangos de un sorteo en la nube antes de
     * registrar los locales. Retorna diagnóstico específico si falla.
     */
    public CloudResult limpiarRangos(int idSorteo) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("id_sorteo", idSorteo);
            System.out.println("[PlazaCloud] Eliminando rangos del sorteo " + idSorteo + ": POST " + urlRangosLimpiar);

            HttpRaw raw = ejecutarPostRaw(urlRangosLimpiar, payload.toString());

            // Caso exitoso: 2xx con body JSON
            if (raw.code >= 200 && raw.code < 300 && !raw.body.isEmpty()) {
                JSONObject resp = new JSONObject(raw.body);
                String estado = resp.optString("estado", "");
                int eliminados = resp.optInt("rangos_eliminados", 0);
                String msg = "OK".equals(estado)
                        ? eliminados + " rango(s) eliminado(s) de la nube."
                        : resp.optString("mensaje", "Error desconocido en limpiar_rangos.");
                return new CloudResult("OK".equals(estado), estado, msg, resp);
            }

            // Diagnóstico específico por código HTTP
            String detalle;
            switch (raw.code) {
                case 0:
                    detalle = "Sin conexión con el servidor APEX. Verifique red y URL.";
                    break;
                case 400:
                    detalle = "HTTP 400 – Solicitud inválida. El PL/SQL recibió un payload malformado.\n"
                            + (raw.body.isEmpty() ? "" : "Detalle: " + raw.body);
                    break;
                case 401:
                    detalle = "HTTP 401 – No autorizado. Verifique usuario/contraseña ORDS en configuración.";
                    break;
                case 403:
                    detalle = "HTTP 403 – Acceso denegado al módulo ORDS 'api_boletas'.";
                    break;
                case 404:
                    detalle = "HTTP 404 – Endpoint no encontrado.\n"
                            + "Verifique que el template 'rangos/limpiar' esté publicado\n"
                            + "en el módulo 'api_boletas' de APEX ORDS.\n"
                            + "URL usada: " + urlRangosLimpiar;
                    break;
                case 405:
                    detalle = "HTTP 405 – Método POST no permitido.\n"
                            + "Verifique que el HANDLER del template 'rangos/limpiar' sea de tipo POST.";
                    break;
                case 500:
                    detalle = "HTTP 500 – Error interno PL/SQL en APEX.\n"
                            + (raw.body.isEmpty()
                                ? "Cuerpo vacío: posible excepción no manejada en PKG_BOLETAS_API.limpiar_rangos.\n"
                                  + "Compruebe que el PACKAGE BODY esté compilado sin errores."
                                : "Detalle APEX: " + raw.body);
                    break;
                case 555:
                    detalle = diagnosticar555LimpiarRangos(raw.body);
                    break;
                default:
                    if (raw.code > 0)
                        detalle = "HTTP " + raw.code + (raw.body.isEmpty() ? " (sin cuerpo)" : " – " + raw.body);
                    else
                        detalle = "No se obtuvo respuesta. Posible timeout o error de red.";
            }
            return new CloudResult(false, "ERROR", detalle, null);

        } catch (java.net.ConnectException ex) {
            return new CloudResult(false, "ERROR",
                    "No se pudo conectar al servidor: " + ex.getMessage(), null);
        } catch (java.net.SocketTimeoutException ex) {
            return new CloudResult(false, "ERROR",
                    "Timeout agotado (" + config.timeoutMs + " ms). El servidor no respondió.", null);
        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error limpiando rangos: " + ex.getMessage());
            ex.printStackTrace();
            return new CloudResult(false, "ERROR", ex.getMessage(), null);
        }
    }

    /**
     * Interpreta el cuerpo de un HTTP 555 (UserDefinedResourceError de ORDS)
     * para el endpoint rangos/limpiar y devuelve un mensaje legible.
     */
    private static String diagnosticar555LimpiarRangos(String body) {
        String causa = "";
        String oErrorCode = "";
        try {
            if (!body.isEmpty()) {
                org.json.JSONObject j = new org.json.JSONObject(body);
                causa      = j.optString("cause", "");
                oErrorCode = j.optString("o:errorCode", "");
            }
        } catch (Exception ignored) { /* si el body no es JSON usamos el texto plano */ }

        // PLS-00306: número o tipo de argumentos incorrecto en LIMPIAR_RANGOS
        if (causa.contains("PLS-00306") || causa.contains("wrong number or types of arguments")) {
            return "HTTP 555 – Error en APEX ORDS al ejecutar rangos/limpiar.\n\n"
                 + "Código : UserDefinedResourceError  (" + oErrorCode + ")\n"
                 + "Causa  : ORA-06550 / PLS-00306 – Número o tipo de argumentos incorrecto\n"
                 + "         en la llamada a PKG_BOLETAS_API.LIMPIAR_RANGOS.\n\n"
                 + "Diagnóstico:\n"
                 + "  La firma del procedimiento LIMPIAR_RANGOS en la base de datos\n"
                 + "  no coincide con lo que el handler ORDS le está pasando.\n\n"
                 + "Pasos para corregir en Oracle APEX:\n"
                 + "  1. SQL Workshop > SQL Commands:\n"
                 + "       SELECT * FROM USER_ERRORS WHERE NAME = 'PKG_BOLETAS_API';\n"
                 + "     Si hay errores, recompile el PACKAGE BODY.\n"
                 + "  2. RESTful Services > api_boletas > rangos/limpiar > Handler POST:\n"
                 + "     Verifique que el bloque PL/SQL llama a LIMPIAR_RANGOS\n"
                 + "     con exactamente los parámetros que declara el PACKAGE.";
        }

        // Mensaje genérico para otros errores 555
        return "HTTP 555 – Error de recurso definido por el usuario (ORDS).\n"
             + "Código : " + oErrorCode + "\n"
             + (causa.isEmpty() ? "" : "Causa  : " + causa + "\n")
             + "Acción : Revise el PACKAGE BODY PKG_BOLETAS_API en Oracle APEX\n"
             + "         y verifique que el template 'rangos/limpiar' esté publicado.";
    }

    // =========================================================================
    // CONSULTAR SORTEO CON RANGOS
    // =========================================================================
    /**
     * Consulta un sorteo por su ID y retorna sus datos junto con los rangos de
     * premio activos registrados en la nube.
     *
     * @param idSorteo ID del sorteo a consultar
     * @return SorteoDetalle con la info y los rangos, o null si falla
     */
    public SorteoDetalle consultarSorteoConRangos(int idSorteo) {
        if (idSorteo <= 0) {
            System.err.println("[PlazaCloud] ID de sorteo inválido: " + idSorteo);
            return null;
        }
        try {
            String url = urlSorteoDetalle + idSorteo;
            System.out.println("[PlazaCloud] Consultando sorteo: GET " + url);

            JSONObject resp = ejecutarGet(url);
            if (resp == null) return null;

            String estado = resp.optString("estado", "");
            if (!"OK".equals(estado)) {
                System.err.println("[PlazaCloud] " + resp.optString("mensaje", estado));
                return null;
            }

            java.util.List<RangoPremio> rangos = new java.util.ArrayList<>();
            JSONArray arr = resp.optJSONArray("rangos");
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject r = arr.getJSONObject(i);
                    rangos.add(new RangoPremio(
                            r.getInt("rango_inicial"),
                            r.getInt("rango_final"),
                            r.optString("mensaje_premio", ""),
                            r.optString("fecha_vigencia", null),
                            r.optInt("prioridad", 1)));
                }
            }

            return new SorteoDetalle(
                    resp.getInt("id_sorteo"),
                    resp.optString("nombre_sorteo", ""),
                    resp.optString("estado_sorteo", "ACTIVO"),
                    rangos);

        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error consultando sorteo: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // =========================================================================
    // CONSULTAR ESTADO BOLETA
    // =========================================================================
    /**
     * Consulta el estado de redención de una boleta por su número y sorteo.
     *
     * @param numeroBoleta Número de la boleta (ej: "0023" o "23")
     * @param idSorteo     ID del sorteo al que pertenece
     * @return EstadoBoleta con el resultado, o null si falla
     */
    public EstadoBoleta consultarEstadoBoleta(String numeroBoleta, int idSorteo) {
        if (numeroBoleta == null || numeroBoleta.trim().isEmpty()) {
            System.err.println("[PlazaCloud] Número de boleta vacío.");
            return null;
        }
        if (idSorteo <= 0) {
            System.err.println("[PlazaCloud] ID de sorteo inválido para consulta de boleta: " + idSorteo);
            return null;
        }
        try {
            // Normalizar a 4 dígitos
            String numNorm = numeroBoleta.trim();
            try {
                numNorm = String.format("%04d", Integer.parseInt(numNorm));
            } catch (NumberFormatException ignored) {}

            String url = urlBoletaEstado + numNorm + "/" + idSorteo;
            System.out.println("[PlazaCloud] Consultando estado boleta: GET " + url);

            JSONObject resp = ejecutarGet(url);
            if (resp == null) return null;

            String estado = resp.optString("estado", "");
            if (!"OK".equals(estado)) {
                String msg = resp.optString("mensaje", "No encontrada.");
                return new EstadoBoleta(numeroBoleta, "ERROR", null, null, msg);
            }

            return new EstadoBoleta(
                    resp.optString("numero_boleta", numeroBoleta),
                    resp.optString("estado_redencion", "NO_CONSULTADA"),
                    resp.optString("fecha_generacion", null),
                    resp.isNull("fecha_redencion") ? null : resp.optString("fecha_redencion"),
                    null);

        } catch (Exception ex) {
            System.err.println("[PlazaCloud] Error consultando estado boleta: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    // =========================================================================
    // MÉTODOS HTTP INTERNOS
    // =========================================================================

    /** Resultado bruto HTTP: código de respuesta y cuerpo como texto. */
    private static class HttpRaw {
        final int    code;
        final String body;
        HttpRaw(int code, String body) { this.code = code; this.body = body != null ? body : ""; }
    }

    /**
     * POST que retorna código HTTP + cuerpo sin interpretar, para diagnóstico detallado.
     * No lanza excepción por código HTTP — sólo por errores de red/IO.
     */
    private HttpRaw ejecutarPostRaw(String urlStr, String jsonPayload) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(config.timeoutMs);
        conn.setReadTimeout(config.timeoutMs);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        configurarAutenticacion(conn);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        java.io.InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
        String body = leerRespuesta(is);
        System.out.println("[PlazaCloud] POST " + urlStr + " -> HTTP " + code
                + (body.isEmpty() ? " | (cuerpo vacío)" : " | " + body));
        return new HttpRaw(code, body);
    }

    private JSONObject ejecutarGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(config.timeoutMs);
        conn.setReadTimeout(config.timeoutMs);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        configurarAutenticacion(conn);

        int code = conn.getResponseCode();
        String body = leerRespuesta(code >= 400 ? conn.getErrorStream() : conn.getInputStream());

        System.out.println("[PlazaCloud] GET " + urlStr + " -> HTTP " + code);
        if (body != null && !body.isEmpty()) {
            System.out.println("[PlazaCloud] Respuesta: " + body);
        }

        if (code >= 200 && code < 300 && body != null && !body.isEmpty()) {
            return new JSONObject(body);
        }
        return null;
    }

    private JSONObject ejecutarPost(String urlStr, String jsonPayload) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(config.timeoutMs);
        conn.setReadTimeout(config.timeoutMs);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        configurarAutenticacion(conn);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] bytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
        }

        int code = conn.getResponseCode();
        String body = leerRespuesta(code >= 400 ? conn.getErrorStream() : conn.getInputStream());

        System.out.println("[PlazaCloud] POST " + urlStr + " -> HTTP " + code);
        if (body != null && !body.isEmpty()) {
            System.out.println("[PlazaCloud] Respuesta: " + body);
        }

        if ((code >= 200 && code < 300) && body != null && !body.isEmpty()) {
            return new JSONObject(body);
        }
        return null;
    }

    private void configurarAutenticacion(HttpURLConnection conn) {
        if ("basic".equals(config.authMode)) {
            String credenciales = config.authUser + ":" + config.authPassword;
            String token = Base64.getEncoder().encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + token);
        } else if ("bearer".equals(config.authMode)) {
            conn.setRequestProperty("Authorization", "Bearer " + config.authToken);
        }
    }

    private String leerRespuesta(java.io.InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
