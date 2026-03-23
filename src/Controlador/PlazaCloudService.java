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

        System.out.println("[PlazaCloud] URLs configuradas:");
        System.out.println("[PlazaCloud]   plaza/validar -> " + urlPlazaValidar);
        System.out.println("[PlazaCloud]   sorteo/crear  -> " + urlSorteoCrear);
        System.out.println("[PlazaCloud]   sorteo/ultimo -> " + urlSorteoUltimo);
        System.out.println("[PlazaCloud]   rangos/reg    -> " + urlRangosRegistrar);
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

    // =========================================================================
    // MÉTODOS HTTP INTERNOS
    // =========================================================================

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
