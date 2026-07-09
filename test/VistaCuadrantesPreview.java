import Modelo.Boleta;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.Image;
import java.io.FileOutputStream;
import java.util.ArrayList;

/** Harness de previsualización del reporte de CUADRANTES (tipo 4, 20 oportunidades). */
public class VistaCuadrantesPreview {
    public static void main(String[] args) throws Exception {
        String base = "/Boletas/";
        String out = args.length > 0 ? args[0] : "/tmp/preview_cuadrantes.pdf";

        Document document = new Document(new Rectangle(792, 612)); // Carta HORIZONTAL (landscape)
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(out));
        document.open();
        PdfContentByte canvas = writer.getDirectContent();

        Boleta bol = new Boleta();
        bol.setMostrarQrGanador(true); // mostrar ambos QR en la vista previa
        Image img = Image.getInstance(base + "Imagenes/Estrellas_3.jpeg");
        Image pre = Image.getInstance(base + "Imagenes/Premio.jpeg");

        Image logoFb = null, logoWa = null;
        try { logoFb = Image.getInstance(base + "Imagenes/Facebook.png"); } catch (Exception e) {}
        try { logoWa = Image.getInstance(base + "Imagenes/Whatsapp.png"); } catch (Exception e) {}

        // 160 números de prueba (8 boletas x 20)
        ArrayList<String> stmp = new ArrayList<>();
        for (int i = 0; i < 200; i++) stmp.add(String.format("%05d", 10000 + i));

        String titulo = "SORTEO GOMEZ PALACIO DURANGO";
        String fecha  = "25-06-2026";
        String valor  = "20 PESOS";
        String m1 = "CADUCIDAD 10 AM";                              // caducidad
        String m2 = "TACHONES-BORRONES ENMENDADURAS ALTERACIONES";  // aviso 1
        String m3 = "";                                             // aviso 2
        String m4 = "SE ANULA EL BOLETO";                           // aviso rojo
        String m5 = "SOMOS FUENTE DE EMPLEO";                       // vertical
        String m6 = "", m7 = "", m8 = "", m9 = "";
        String fb = "Sorteo Gomez P. Dgo";
        String wa = "871 275 4325";

        int maxCol = 4, maxFil = 2;
        float ancho = 184, alto = 283, paso_x = 190, paso_y = 289;
        float xi = 18, yi = 612 - 16 - alto;
        int idx = 0;
        for (int f = 0; f < maxFil; f++) {
            for (int c = 0; c < maxCol; c++) {
                float x = xi + c * paso_x;
                float y = yi - f * paso_y;
                bol.drawRectangleCuadrantes(canvas, x, y, ancho, alto, 5, 5,
                        titulo, fecha, valor,
                        m1, m2, m3, m4, m5, m6, m7, m8, m9,
                        20, 1, stmp, idx,
                        img, pre, "QR-INFO-" + idx, "QR-PREMIO-" + idx,
                        "SORT01", String.format("%011d", 23212160623022L + idx),
                        logoFb, logoWa, fb, wa, 0.25f);
                idx += 20;
            }
        }
        document.close();
        System.out.println("OK -> " + out);
    }
}
