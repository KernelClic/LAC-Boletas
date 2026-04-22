# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**LAC-Boletas** is a Java desktop application for generating lottery tickets (boletas) with integrated cloud synchronization to Oracle ORDS. The application follows an MVC architecture with three layers: Vista (View), Modelo (Model), and Controlador (Controller).

- Generate PDF tickets with randomized numbers and QR codes
- Support multiple drawings (sorteos) and prize ranges (rangos de premio)
- Sync locally-generated tickets to Oracle Cloud via REST API
- Validate QR codes and manage ticket redemption status

**Technology Stack**: Java 8, Apache Ant (NetBeans), Swing, iText 5.0.5, JasperReports 6.7.0, ZXing 3.5.1, SQLite 3.23.1 (local) + Oracle via ORDS REST API, org.json 20231013

### Data Flow
1. **Local Tier**: User generates tickets in Swing GUI → iText renders PDF with QR codes → tickets stored in SQLite (`boletas.db`)
2. **Sync Daemon**: Scheduled background thread monitors `boleta_local_sync` for `PENDIENTE_SYNC` records → batches tickets → sends via HTTP POST to ORDS
3. **Cloud Tier**: Oracle ORDS API receives tickets, validates plaza/sorteo, stores in `TRP_BOLETAS`
4. **Redemption**: Users scan QR codes → ORDS validates token, checks prize ranges, marks as `CONSULTADA`

---

## Architecture & Key Components

### Vista Layer (`src/Vista/`)
- **Generador_Boletas.java** (1642 lines): Main Swing JFrame — handles ticket generation configuration, random number generation (no duplicates), and PDF generation coordination.

### Modelo Layer (`src/Modelo/`)
- **Boleta.java** (660 lines): PDF rendering engine
  - `drawRectangle()`: Renders individual ticket with scaled fonts, borders, text, images
  - `generarQRImage()`: Generates ZXing QR codes with PNG export for crisp printing
  - Supports 1-10 opportunities per ticket with dynamic layout positioning
- **Tabla.java** (345 lines): Legacy data model for ticket metadata with 25 configurable number fields (n1-n25)

### Controlador Layer (`src/Controlador/`)

#### BoletaCloudSync.java (465 lines) — Core Sync Engine
**Configuration** (`BoletaCloudSync.Config`) reads from `config/boletas-sync.properties` or system properties:
- Key settings: `boletas.sync.ords.url`, `boletas.sync.sqlite.url`, `boletas.sync.auth.mode`, `boletas.sync.batch.max` (default 500)
- Supports basic auth (user:pass) or bearer token auth

Config loading cascade:
1. System property: `-Dboletas.sync.config.path=/path/to/file`
2. Environment variable: `BOLETAS_SYNC_CONFIG_PATH`
3. Default file: `config/boletas-sync.properties`

**Sync Workflow**:
1. `startSyncDaemon()`: Spawns `ScheduledExecutorService` running `runSyncCycle()` every 60 seconds
2. `syncNow()`: Triggered immediately after ticket generation
3. `runSyncCycle()`: Queries `boleta_local_sync` for `PENDIENTE_SYNC` → batches into JSON `{"id_sorteo": N, "boletas": [...]}` → POST to ORDS → on success marks `estado='OK'`

**Other controllers**:
- **ConectorSqlite.java**: JDBC wrapper — `insertarSincronizacion()`, `limpiarTabla()`
- **PlazaCloudService.java**: REST client for plaza/sorteo management; validates plaza by `codigo_seguro`
- **GeneradorQR.java**: UUID token generation; QR points to `https://poco.absapex.net/apex/api_boletas/api_boletas/v1/qr/consultar/{tokenUUID}`
- **AccesoAleatorio.java**: Legacy file I/O with OS-specific hardcoded paths (Windows/Mac/Unix) for `boletas.db`, report, and image directories

### Database Schema

**SQLite (Local)** — `boleta_local_sync`:
- Columns: `id_sorteo_nube`, `numero_boleta`, `numeros_oportunidades`, `qr_token`, `estado` (PENDIENTE_SYNC|OK)

**Oracle (Cloud)**:
- `CNL_PLAZAS` — plaza registry with unique `codigo_seguro`
- `CNL_SORTEOS` — drawings per plaza
- `CNL_RANGOS_PREMIO` — prize ranges with `PRIORIDAD` ordering (higher priority wins when ranges overlap)
- `TRP_BOLETAS` — all tickets with `estado_redencion` (NO_CONSULTADA|CONSULTADA) and unique `qr_token`
- `TRD_CONSULTAS_QR` / `AUD_INTENTOS_QR` — audit log and fraud tracking

### Configuration Files
- `config/boletas-sync.properties` — local sync configuration (Git-ignored)
- `config/boletas-sync.example.properties` — template with all available properties

---

## Building & Running

```bash
# Requires Apache Ant and JDK 8+
ant clean build        # Compile; outputs dist/Boletas_v5.jar
ant run                # Launch Swing GUI
ant test               # Run smoke test (BoletaCloudSyncSmokeTest)
```

Direct run (requires JAR + libs):
```bash
java -cp "dist/Boletas_v5.jar:lib/*" Vista.Generador_Boletas
```

### Database Setup
1. **Local SQLite**: `db/boletas.db` is auto-created if missing
2. **Cloud Oracle**: Run `db/schema_plazas_sorteos.sql` then `db/api_boletas_ords.sql` on ORDS database
3. **Test Data**: `db/seed_test_boletas.sql` populates demo plazas/sorteos

---

## Key Implementation Details

### QR Code Generation
- ZXing with `ErrorCorrectionLevel.M`, UTF-8, 2-module quiet zone
- 200×200 px native → scaled to PDF points; exported as PNG to avoid print interpolation artifacts

### Sync Auth
- Modes: `none` (default), `basic` (Base64 user:pass), `bearer`
- Header: `Authorization: Basic <base64>` or `Authorization: Bearer <token>`

### Batching & Retry
- Batches up to 500 tickets (configurable via `boletas.sync.batch.max`)
- Failed syncs are NOT retried immediately — daemon waits 60s, then retries automatically

### Layout Scaling
- `Boleta.drawRectangle()` uses responsive scaling: `scaleX = ancho / 260.0`, `scaleY = alto / 145.0`
- Font sizes, positioning, and image dimensions scale proportionally

### Prize Range Priority
- If a ticket falls in multiple ranges, the one with the highest `PRIORIDAD` value wins

---

## Testing

**BoletaCloudSyncSmokeTest.java** — integration test that:
1. Creates a temporary SQLite database
2. Spawns a mock HTTP server to simulate ORDS
3. Inserts pending tickets into local database
4. Runs a sync cycle
5. Asserts all tickets are marked `OK`

```bash
cd /datos/repo/kernelclic/LAC-Boletas
ant test
```

---

## Known Limitations

- **AccesoAleatorio.java** contains hardcoded OS-specific paths (`c:\Boletas\db\`, `/Boletas/db/`) — should be replaced with relative/configurable paths
- **Tabla.java** uses repetitive field definitions (n1-n25) instead of collections
- Sync daemon is polling-based (60s) rather than event-driven
- NetBeans/Ant project structure — all JAR dependencies in `lib/`; `javac.classpath` in `nbproject/project.properties`
