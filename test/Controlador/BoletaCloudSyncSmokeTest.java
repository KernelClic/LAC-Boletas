package Controlador;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONObject;

public class BoletaCloudSyncSmokeTest {

    public static void main(String[] args) throws Exception {
        File dbFile = File.createTempFile("boletas-sync-smoke-", ".db");
        String sqliteUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        AtomicInteger requests = new AtomicInteger();
        AtomicInteger boletasRecibidas = new AtomicInteger();
        final String expectedAuth = "Basic "
                + Base64.getEncoder().encodeToString("demo:secreta".getBytes(StandardCharsets.UTF_8));

        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/ords/boletas/batch", exchange -> {
            try {
                manejarBatch(exchange, expectedAuth, requests, boletasRecibidas);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    responder(exchange, 500, "{\"status\":\"error\"}");
                } catch (Exception responseEx) {
                    responseEx.printStackTrace();
                }
            }
        });
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();

        try {
            crearSchema(sqliteUrl);
            insertarPendiente(sqliteUrl, 321, "0001", "uuid-0001");
            insertarPendiente(sqliteUrl, 321, "0002", "uuid-0002");
            insertarPendiente(sqliteUrl, 321, "0003", "uuid-0003");

            BoletaCloudSync.Config config = new BoletaCloudSync.Config(
                    "http://127.0.0.1:" + server.getAddress().getPort() + "/ords/boletas/batch",
                    sqliteUrl,
                    5000,
                    2,
                    true,
                    "basic",
                    "demo",
                    "secreta",
                    "");

            BoletaCloudSync sync = new BoletaCloudSync(config);
            sync.runSyncCycle();

            int ok = contarPorEstado(sqliteUrl, "OK");
            int pendientes = contarPorEstado(sqliteUrl, "PENDIENTE_SYNC");

            System.out.println("[SmokeTest] Requests HTTP recibidos: " + requests.get());
            System.out.println("[SmokeTest] Boletas recibidas por ORDS simulado: " + boletasRecibidas.get());
            System.out.println("[SmokeTest] Registros OK en SQLite: " + ok);
            System.out.println("[SmokeTest] Registros pendientes en SQLite: " + pendientes);

            if (requests.get() != 2) {
                throw new IllegalStateException("Se esperaban 2 requests HTTP y llegaron " + requests.get());
            }
            if (boletasRecibidas.get() != 3) {
                throw new IllegalStateException(
                        "Se esperaban 3 boletas recibidas por el ORDS simulado y llegaron " + boletasRecibidas.get());
            }
            if (ok != 3 || pendientes != 0) {
                throw new IllegalStateException(
                        "Estado final inválido en SQLite. OK=" + ok + ", PENDIENTE_SYNC=" + pendientes);
            }

            System.out.println("[SmokeTest] Resultado: OK");
        } finally {
            server.stop(0);
            dbFile.delete();
        }
    }

    private static void manejarBatch(HttpExchange exchange, String expectedAuth, AtomicInteger requests,
            AtomicInteger boletasRecibidas) throws Exception {
        requests.incrementAndGet();
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        if (!expectedAuth.equals(authorization)) {
            responder(exchange, 401, "{\"status\":\"unauthorized\"}");
            return;
        }

        String payload = leer(exchange.getRequestBody());
        JSONObject json = new JSONObject(payload);
        JSONArray boletas = json.getJSONArray("boletas");
        boletasRecibidas.addAndGet(boletas.length());

        System.out.println("[SmokeORDS] Autenticacion recibida: OK");
        System.out.println("[SmokeORDS] id_sorteo recibido: " + json.getInt("id_sorteo"));
        System.out.println("[SmokeORDS] cantidad de boletas recibidas: " + boletas.length());

        responder(exchange, 201, "{\"status\":\"registered\",\"received\":" + boletas.length() + "}");
    }

    private static void responder(HttpExchange exchange, int status, String body) throws Exception {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    private static String leer(InputStream inputStream) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private static void crearSchema(String sqliteUrl) throws Exception {
        try (Connection conn = DriverManager.getConnection(sqliteUrl);
                Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE boleta_local_sync ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "id_sorteo_nube INTEGER NOT NULL,"
                    + "numero_boleta TEXT NOT NULL,"
                    + "qr_token TEXT NOT NULL UNIQUE,"
                    + "fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "sync_status TEXT DEFAULT 'PENDIENTE_SYNC',"
                    + "fecha_sync DATETIME)");
        }
    }

    private static void insertarPendiente(String sqliteUrl, int idSorteo, String numero, String qrToken) throws Exception {
        try (Connection conn = DriverManager.getConnection(sqliteUrl);
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO boleta_local_sync(id_sorteo_nube, numero_boleta, qr_token) VALUES(?,?,?)")) {
            pst.setInt(1, idSorteo);
            pst.setString(2, numero);
            pst.setString(3, qrToken);
            pst.executeUpdate();
        }
    }

    private static int contarPorEstado(String sqliteUrl, String estado) throws Exception {
        try (Connection conn = DriverManager.getConnection(sqliteUrl);
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT COUNT(*) FROM boleta_local_sync WHERE sync_status = ?")) {
            pst.setString(1, estado);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
