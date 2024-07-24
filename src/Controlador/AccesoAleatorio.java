package Controlador;

import java.io.*;
import Modelo.*;
import java.util.Date;

public class AccesoAleatorio {

    private static RandomAccessFile flujo;
    private static int numeroRegistros;
    private static final int tamañoRegistro = 110;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static final String RutaWinDB = "c:\\Boletas\\db\\";
    private static final String RutaLinDB = "/Boletas/db/";

    private static final String RutaWinREP = "c:\\Boletas\\Reportes\\";
    private static final String RutaLinREP = "/Boletas/Reportes/";

    private static final String RutaWinIMG = "c:\\Boletas\\Imagenes\\";
    private static final String RutaLinIMG = "/Boletas/Imagenes/";

    private static final String FileRutaWindb = "c:\\windows\\system\\windll.dll";
    private static final String FileRutaLindb = "/usr/readme.txt";

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac"));
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    public static boolean getLicencia() {
        File fichero = null;
        Date fecha = new Date();
        int dia, mes;
        dia = fecha.getDay();
        mes = fecha.getMonth();
        if (mes >= 3) {
            if (dia > 15) {
                if (isWindows()) {
                    fichero = new File(FileRutaWindb);
                } else if (isMac()) {
                    fichero = new File(FileRutaLindb);

                } else if (isUnix()) {
                    fichero = new File(FileRutaLindb);
                }
                return fichero.delete();
            }
        }
        return false;
    }

    public static String getRutaFileRep() {
        if (isWindows()) {
            return RutaWinREP;
        } else if (isMac()) {
            return RutaLinREP;
        } else if (isUnix()) {
            return RutaLinREP;
        }
        return null;
    }

    public static String getRutaFileDB() {
        if (isWindows()) {
            return RutaWinDB;
        } else if (isMac()) {
            return RutaLinDB;
        } else if (isUnix()) {
            return RutaLinDB;
        }
        return null;
    }

    public static String getRutaFiledb() {
        if (isWindows()) {
            return FileRutaWindb;
        } else if (isMac()) {
            return FileRutaLindb;
        } else if (isUnix()) {
            return FileRutaLindb;
        }
        return null;
    }

    public static String getRutaImagenes() {
        if (isWindows()) {
            return RutaWinIMG;
        } else if (isMac()) {
            return RutaLinIMG;
        } else if (isUnix()) {
            return RutaLinIMG;
        }
        return null;
    }

    public static boolean buscarFile(File archivo) throws IOException {
        return !(!archivo.exists() || !archivo.isFile());
    }

    public static void crearFileTablas(File archivo) throws IOException {
        if (archivo.exists() && !archivo.isFile()) {
            throw new IOException(archivo.getName() + " no es un archivo");
        }
        flujo = new RandomAccessFile(archivo, "rw");
        numeroRegistros = (int) Math.ceil(
                (double) flujo.length() / (double) tamañoRegistro);
    }

    public static void cerrar() throws IOException {
        flujo.close();
    }

    public static boolean setTabla(int i, Tabla tabla) throws IOException {
        if (i >= 0 && i <= getNumeroRegistros()) {
            if (tabla.getTamaño() > tamañoRegistro) {
                System.out.println("\nTamaño de registro excedido.");
            } else {
                flujo.seek(i * tamañoRegistro);
                flujo.writeInt(tabla.getNumTabla());
                flujo.writeBoolean(tabla.isActivo());
                flujo.writeInt(tabla.getN1());
                flujo.writeInt(tabla.getN2());
                flujo.writeInt(tabla.getN3());
                flujo.writeInt(tabla.getN4());
                flujo.writeInt(tabla.getN5());
                flujo.writeInt(tabla.getN6());
                flujo.writeInt(tabla.getN7());
                flujo.writeInt(tabla.getN8());
                flujo.writeInt(tabla.getN9());
                flujo.writeInt(tabla.getN10());
                flujo.writeInt(tabla.getN11());
                flujo.writeInt(tabla.getN12());
                flujo.writeInt(tabla.getN13());
                flujo.writeInt(tabla.getN14());
                flujo.writeInt(tabla.getN15());
                flujo.writeInt(tabla.getN16());
                flujo.writeInt(tabla.getN17());
                flujo.writeInt(tabla.getN18());
                flujo.writeInt(tabla.getN19());
                flujo.writeInt(tabla.getN20());
                flujo.writeInt(tabla.getN21());
                flujo.writeInt(tabla.getN22());
                flujo.writeInt(tabla.getN23());
                flujo.writeInt(tabla.getN24());
                flujo.writeInt(tabla.getN25());
                return true;
            }
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
        }
        return false;
    }

    private static int buscarRegistroInactivo() throws IOException {
        String nombre;
        for (int i = 0; i < getNumeroRegistros(); i++) {
            flujo.seek(i * tamañoRegistro);
            if (!getTabla(i).isActivo()) {
                return i;
            }
        }
        return -1;
    }

    public static boolean eliminarTabla(int aEliminar) throws IOException {
        int pos = buscarRegistro(aEliminar);
        if (pos == -1) {
            return false;
        }
        Tabla tablaEliminada = getTabla(pos);
        tablaEliminada.setActivo(false);
        setTabla(pos, tablaEliminada);
        return true;
    }

    public static void compactarArchivo(File archivo) throws IOException {
        crearFileTablas(archivo); // Abrimos el flujo.
        Tabla[] listado = new Tabla[numeroRegistros];
        for (int i = 0; i < numeroRegistros; ++i) {
            listado[i] = getTabla(i);
        }
        cerrar(); // Cerramos el flujo.
        archivo.delete(); // Borramos el archivo.

        File tempo = new File("temporal.dat");
        crearFileTablas(tempo); // Como no existe se crea.
        for (Tabla p : listado) {
            if (p.isActivo()) {
                añadirTabla(p);
            }
        }
        cerrar();

        tempo.renameTo(archivo); // Renombramos.
    }

    public static void añadirTabla(Tabla tabla) throws IOException {
        int inactivo = buscarRegistroInactivo();
        if (setTabla(inactivo == -1 ? numeroRegistros : inactivo, tabla)) {
            numeroRegistros++;
        }
    }

    public static int getNumeroRegistros() {
        return numeroRegistros;
    }

    public static Tabla getTabla(int i) throws IOException {
        if (i >= 0 && i <= getNumeroRegistros()) {
            flujo.seek(i * tamañoRegistro);
            return new Tabla(flujo.readInt(), flujo.readBoolean(),
                    flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), //B
                    flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), //I
                    flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), //N
                    flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), //G
                    flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt(), flujo.readInt());//O
        } else {
            System.out.println("\nNúmero de registro fuera de límites.");
            return null;
        }
    }

    public static int buscarRegistro(int buscado) throws IOException {
        Tabla p;
        if (buscado == 0) {
            return -1;
        }
        for (int i = 0; i < getNumeroRegistros(); i++) {
            flujo.seek(i * tamañoRegistro);
            p = getTabla(i);
            if (p.getNumTabla() == buscado) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarTabla(int[] buscado) throws IOException {
        Tabla p;
        int bingo[] = new int[25];
        if (buscado == null) {
            return -1;
        }

        for (int i = 0; i < getNumeroRegistros(); i++) {
            flujo.seek(i * tamañoRegistro);
            p = getTabla(i);
            bingo[0] = p.getN1();
            bingo[1] = p.getN2();
            bingo[2] = p.getN3();
            bingo[3] = p.getN4();
            bingo[4] = p.getN5();
            bingo[5] = p.getN6();
            bingo[6] = p.getN7();
            bingo[7] = p.getN8();
            bingo[8] = p.getN9();
            bingo[9] = p.getN10();
            bingo[10] = p.getN11();
            bingo[11] = p.getN12();
            bingo[12] = p.getN13();
            bingo[13] = p.getN14();
            bingo[14] = p.getN15();
            bingo[15] = p.getN16();
            bingo[16] = p.getN17();
            bingo[17] = p.getN18();
            bingo[18] = p.getN19();
            bingo[19] = p.getN20();
            bingo[20] = p.getN21();
            bingo[21] = p.getN22();
            bingo[22] = p.getN23();
            bingo[23] = p.getN24();
            bingo[24] = p.getN25();

            for (int f = 0; f < 25; f++) {
                for (int c = 0; c < 25; c++) {
                    if (bingo[f] == buscado[c]) {
                        return (i);
                    }
                }
            }
        }
        return 0;
    }

}
