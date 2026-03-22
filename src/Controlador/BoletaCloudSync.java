package Controlador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Motor de sincronización boletas → ORDS (Oracle Cloud).
 *
 * Flujo principal:
 * 1. Al arrancar la app: startSyncDaemon() — revisa cada 60 s los pendientes.
 * 2. Al terminar de generar un lote: syncNow() — dispara sincronización
 * inmediata
 * en un hilo aparte para no bloquear la UI.
 */
public class BoletaCloudSync {

    public static class Config {
        private static final String DEFAULT_CONFIG_PATH = "config/boletas-sync.properties";
        private static final String DEFAULT_ORDS_BATCH_URL = "https://poco.absapex.net/apex/api_boletas/v1/boletas/batch";
        private static final String DEFAULT_SQLITE_URL = "jdbc:sqlite:db/boletas.db";
        private static final int DEFAULT_TIMEOUT_MS = 15_000;
        private static final int DEFAULT_LOTE_MAX = 500;

        final String ordsBatchUrl;
        final String sqliteUrl;
        final int timeoutMs;
        final int loteMax;
        final boolean syncEnabled;
        final String authMode;
        final String authUser;
        final String authPassword;
        final String authToken;

        public Config(String ordsBatchUrl, String sqliteUrl, int timeoutMs, int loteMax,
                boolean syncEnabled, String authMode, String authUser, String authPassword, String authToken) {
            this.ordsBatchUrl = ordsBatchUrl;
            this.sqliteUrl = sqliteUrl;
            this.timeoutMs = timeoutMs;
            this.loteMax = loteMax;
            this.syncEnabled = syncEnabled;
            this.authMode = authMode == null ? "none" : authMode.trim().toLowerCase();
            this.authUser = authUser;
            this.authPassword = authPassword;
            this.authToken = authToken;
        }

        public static Config fromSystem() {
            String configPath = firstNonBlank(System.getProperty("boletas.sync.config.path"),
                    System.getenv("BOLETAS_SYNC_CONFIG_PATH"), DEFAULT_CONFIG_PATH);
            Properties fileProperties = loadPropertiesFile(configPath);

            return new Config(
                    firstNonBlank(System.getProperty("boletas.sync.ords.url"),
                            fileProperties.getProperty("boletas.sync.ords.url"),
                            System.getenv("BOLETAS_ORDS_BATCH_URL"),
                            DEFAULT_ORDS_BATCH_URL),
                    firstNonBlank(System.getProperty("boletas.sync.sqlite.url"),
                            fileProperties.getProperty("boletas.sync.sqlite.url"),
                            System.getenv("BOLETAS_SQLITE_URL"),
                            DEFAULT_SQLITE_URL),
                    parseInt(firstNonBlank(System.getProperty("boletas.sync.timeout.ms"),
                            fileProperties.getProperty("boletas.sync.timeout.ms"),
                            System.getenv("BOLETAS_SYNC_TIMEOUT_MS")), DEFAULT_TIMEOUT_MS),
                    parseInt(firstNonBlank(System.getProperty("boletas.sync.batch.max"),
                            fileProperties.getProperty("boletas.sync.batch.max"),
                            System.getenv("BOLETAS_SYNC_BATCH_MAX")), DEFAULT_LOTE_MAX),
                    parseBoolean(firstNonBlank(System.getProperty("boletas.sync.enabled"),
                            fileProperties.getProperty("boletas.sync.enabled"),
                            System.getenv("BOLETAS_SYNC_ENABLED")), true),
                    firstNonBlank(System.getProperty("boletas.sync.auth.mode"),
                            fileProperties.getProperty("boletas.sync.auth.mode"),
                            System.getenv("BOLETAS_ORDS_AUTH_MODE"),
                            "none"),
                    firstNonBlank(System.getProperty("boletas.sync.auth.user"),
                            fileProperties.getProperty("boletas.sync.auth.user"),
                            System.getenv("BOLETAS_ORDS_AUTH_USER"),
                            ""),
                    firstNonBlank(System.getProperty("boletas.sync.auth.password"),
                            fileProperties.getProperty("boletas.sync.auth.password"),
                            System.getenv("BOLETAS_ORDS_AUTH_PASSWORD"), ""),
                    firstNonBlank(System.getProperty("boletas.sync.auth.token"),
                            fileProperties.getProperty("boletas.sync.auth.token"),
                            System.getenv("BOLETAS_ORDS_AUTH_TOKEN"),
                            ""));
        }

        private static Properties loadPropertiesFile(String configPath) {
            Properties properties = new Properties();
            File file = new File(configPath);
            if (!file.exists()) {
                System.out.println("[CloudSync] Archivo de configuración local no encontrado: " + file.getPath());
                return properties;
            }

            try (FileInputStream input = new FileInputStream(file)) {
                properties.load(input);
                System.out.println("[CloudSync] Configuración local cargada desde: " + file.getPath());
            } catch (Exception ex) {
                System.err.println("[CloudSync] No se pudo leer el archivo de configuración " + file.getPath()
                        + ": " + ex.getMessage());
            }
            return properties;
        }

        private static String firstNonBlank(String... values) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    return value.trim();
                }
            }
            return "";
        }

        private static int parseInt(String value, int defaultValue) {
            try {
                return value == null || value.trim().isEmpty() ? defaultValue : Integer.parseInt(value.trim());
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        }

        private static boolean parseBoolean(String value, boolean defaultValue) {
            return value == null || value.trim().isEmpty() ? defaultValue : Boolean.parseBoolean(value.trim());
        }
    }

    private static class BoletaPendiente {
        final int id;
        final int idSorteo;
        final String numeroBoleta;
        final String qrToken;

        BoletaPendiente(int id, int idSorteo, String numeroBoleta, String qrToken) {
            this.id = id;
            this.idSorteo = idSorteo;
            this.numeroBoleta = numeroBoleta;
            this.qrToken = qrToken;
        }
    }

    private static class HttpResult {
        final int code;
        final String body;

        HttpResult(int code, String body) {
            this.code = code;
            this.body = body;
        }
    }

    // ─── Scheduler para el daemon periódico ────────────────────────────────────
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Config config;

    public BoletaCloudSync() {
        this(Config.fromSystem());
    }

    public BoletaCloudSync(Config config) {
        this.config = config;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PÚBLICO: inicia el daemon periódico (cada 60 s)
    // ──────────────────────────────────────────────────────────────────────────
    public void startSyncDaemon() {
        System.out.println("[CloudSync] Daemon iniciado. syncEnabled=" + config.syncEnabled
                + ", sqliteUrl=" + config.sqliteUrl
                + ", ordsUrl=" + config.ordsBatchUrl
                + ", authMode=" + config.authMode
                + ", revisión cada 60 s.");
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[CloudSync] Daemon → verificando pendientes...");
            ejecutarSincronizacion();
        }, 30, 60, TimeUnit.SECONDS);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PÚBLICO: sincronización inmediata (llamada justo al terminar de generar)
    // ──────────────────────────────────────────────────────────────────────────
    public void syncNow() {
        System.out.println("[CloudSync] Sincronización INMEDIATA disparada por generación de boletas.");
        // Ejecutar en hilo separado para no bloquear el hilo de Swing/EDT
        Thread t = new Thread(() -> ejecutarSincronizacion(), "CloudSync-Inmediata");
        t.setDaemon(true);
        t.start();
    }

    public void runSyncCycle() {
        ejecutarSincronizacion();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PRIVADO: lógica de sincronización (reutilizable por daemon y syncNow)
    // ──────────────────────────────────────────────────────────────────────────
    private void ejecutarSincronizacion() {
        if (!config.syncEnabled) {
            System.out.println("[CloudSync] Sincronización deshabilitada por configuración. No se ejecuta el ciclo.");
            return;
        }
        try {
            sincronizarPendientes();
        } catch (Exception ex) {
            System.err.println("[CloudSync] ERROR general de sincronización: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sincronizarPendientes() throws Exception {
        System.out.println("[CloudSync] Conectando a SQLite local: " + config.sqliteUrl);

        try (Connection conn = DriverManager.getConnection(config.sqliteUrl)) {
            System.out.println("[CloudSync] Conexión SQLite establecida correctamente.");

            // 1. Contar cuántas boletas están pendientes
            int totalPendientes = contarPendientes(conn);
            if (totalPendientes == 0) {
                System.out.println("[CloudSync] No hay boletas pendientes de sincronizar.");
                return;
            }
            System.out.println("[CloudSync] Boletas pendientes encontradas: " + totalPendientes);

            // 2. Procesar por lotes de LOTE_MAX
            int enviadas = 0;
            while (true) {
                System.out.println("[CloudSync] Procesando siguiente lote. max=" + config.loteMax + "...");
                int resultado = procesarLote(conn);
                if (resultado == 0)
                    break;

                enviadas += resultado;
                System.out.println("[CloudSync] Lote completado. Enviadas en este lote: " + resultado
                        + ". Total acumulado: " + enviadas);
            }

            System.out.println("[CloudSync] Sincronización completada. Total enviadas: "
                    + enviadas + " de " + totalPendientes + " pendientes.");
        }
    }

    // ─── Cuenta pendientes ────────────────────────────────────────────────────
    private int contarPendientes(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) AS cnt FROM boleta_local_sync WHERE sync_status='PENDIENTE_SYNC'";
        try (PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            return rs.next() ? rs.getInt("cnt") : 0;
        }
    }

    // ─── Procesa un lote y lo envía al ORDS ──────────────────────────────────
    private int procesarLote(Connection conn) throws Exception {
        List<BoletaPendiente> lote = cargarPendientes(conn);
        if (lote.isEmpty()) {
            return 0;
        }

        JSONArray boletas = new JSONArray();
        int idSorteo = lote.get(0).idSorteo;
        for (BoletaPendiente pendiente : lote) {
            JSONObject item = new JSONObject();
            item.put("numero", pendiente.numeroBoleta);
            item.put("qr_token", pendiente.qrToken);
            boletas.put(item);
        }

        JSONObject payload = new JSONObject();
        payload.put("id_sorteo", idSorteo);
        payload.put("boletas", boletas);

        System.out.println("[CloudSync] Preparando lote para ORDS. cantidad=" + lote.size()
                + ", id_sorteo=" + idSorteo
                + ", primerIdLocal=" + lote.get(0).id
                + ", últimoIdLocal=" + lote.get(lote.size() - 1).id);
        System.out.println("[CloudSync] Enviando lote al ORDS -> " + config.ordsBatchUrl);

        HttpResult httpResult = enviarAOrds(payload.toString());

        if (httpResult.code == 200 || httpResult.code == 201) {
            int actualizadas = marcarComoOk(conn, lote);
            System.out.println("[CloudSync] HTTP " + httpResult.code
                    + ". Registros marcados como OK en SQLite: " + actualizadas + ".");
            if (httpResult.body != null && !httpResult.body.isEmpty()) {
                System.out.println("[CloudSync] Respuesta ORDS: " + httpResult.body);
            }
            return actualizadas;
        } else {
            System.err.println("[CloudSync] ORDS respondió HTTP " + httpResult.code
                    + ". El lote queda pendiente para reintento.");
            return 0;
        }
    }

    private List<BoletaPendiente> cargarPendientes(Connection conn) throws SQLException {
        int idSorteo = obtenerPrimerSorteoPendiente(conn);
        if (idSorteo <= 0) {
            return new ArrayList<>();
        }

        String selectSql = "SELECT id, id_sorteo_nube, numero_boleta, qr_token "
                + "FROM boleta_local_sync "
                + "WHERE sync_status='PENDIENTE_SYNC' AND id_sorteo_nube = ? "
                + "ORDER BY id LIMIT ?";
        List<BoletaPendiente> lote = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(selectSql)) {
            pst.setInt(1, idSorteo);
            pst.setInt(2, config.loteMax);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lote.add(new BoletaPendiente(
                            rs.getInt("id"),
                            rs.getInt("id_sorteo_nube"),
                            rs.getString("numero_boleta"),
                            rs.getString("qr_token")));
                }
            }
        }
        return lote;
    }

    private int obtenerPrimerSorteoPendiente(Connection conn) throws SQLException {
        String sql = "SELECT id_sorteo_nube FROM boleta_local_sync "
                + "WHERE sync_status='PENDIENTE_SYNC' ORDER BY id LIMIT 1";
        try (PreparedStatement pst = conn.prepareStatement(sql);
                ResultSet rs = pst.executeQuery()) {
            return rs.next() ? rs.getInt("id_sorteo_nube") : 0;
        }
    }

    private int marcarComoOk(Connection conn, List<BoletaPendiente> lote) throws SQLException {
        String sql = "UPDATE boleta_local_sync SET sync_status='OK', fecha_sync=CURRENT_TIMESTAMP "
                + "WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (BoletaPendiente pendiente : lote) {
                pst.setInt(1, pendiente.id);
                pst.addBatch();
            }
            int[] resultado = pst.executeBatch();
            return contarActualizados(resultado);
        }
    }

    private int contarActualizados(int[] resultado) {
        int actualizados = 0;
        for (int valor : resultado) {
            if (valor >= 0 || valor == Statement.SUCCESS_NO_INFO) {
                actualizados++;
            }
        }
        return actualizados;
    }

    // ─── Envía el payload JSON al endpoint ORDS y retorna código y body ───────
    private HttpResult enviarAOrds(String jsonPayload) throws Exception {
        URL url = new URL(config.ordsBatchUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(config.timeoutMs);
        conn.setReadTimeout(config.timeoutMs);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        configurarAutenticacion(conn);
        conn.setDoOutput(true);

        System.out.println("[CloudSync] HTTP POST -> " + config.ordsBatchUrl);
        System.out.println("[CloudSync] Autenticación configurada: " + config.authMode.toUpperCase());

        try (OutputStream os = conn.getOutputStream()) {
            byte[] bytes = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
            System.out.println("[CloudSync] Payload enviado correctamente (" + bytes.length + " bytes).");
        }

        int code = conn.getResponseCode();
        String body = leerRespuesta(code >= 400 ? conn.getErrorStream() : conn.getInputStream());
        System.out.println("[CloudSync] Respuesta HTTP recibida: " + code);
        if (body != null && !body.isEmpty()) {
            System.out.println("[CloudSync] Body de respuesta recibido: " + body);
        }

        return new HttpResult(code, body);
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

    private String leerRespuesta(InputStream stream) throws Exception {
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

    // ─── Detener el scheduler limpiamente ────────────────────────────────────
    public void detener() {
        System.out.println("[CloudSync] Deteniendo daemon de sincronización...");
        scheduler.shutdown();
    }
}
