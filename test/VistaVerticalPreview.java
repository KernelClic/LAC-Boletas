import Modelo.Boleta;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.Image;
import java.io.FileOutputStream;
import java.util.ArrayList;

/** Harness de previsualización del reporte vertical (tipo 3). */
public class VistaVerticalPreview {
    public static void main(String[] args) throws Exception {
        String base = "/datos/repo/kernelclic/LAC-Boletas/";
        String out = args.length > 0 ? args[0] : "/tmp/claude-1000/-datos-repo-kernelclic-LAC-Boletas/854bced2-d670-4998-bb79-41500de9ea68/scratchpad/preview_vertical.pdf";

        Document document = new Document(new Rectangle(612, 792)); // Carta vertical
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(out));
        document.open();
        PdfContentByte canvas = writer.getDirectContent();

        Boleta bol = new Boleta();
        Image img = Image.getInstance(base + "Imagenes/Estrellas_1.jpeg");
        Image pre = Image.getInstance(base + "Dinero.jpeg");

        // 60 números de prueba
        ArrayList<String> stmp = new ArrayList<>();
        for (int i = 0; i < 80; i++) stmp.add(String.format("%05d", 10000 + i));

        String m1 = "El aporte lo hace participe del sorteo";
        String m2 = "Responsable: Loteria La Estrella";
        String m3 = "Tel: 300 000 0000";
        String m4 = "Premio mayor en efectivo";
        String m5 = "Juegue con responsabilidad";
        String m6 = "Valido solo para la fecha indicada";
        String m7 = "No se cambian boletas";
        String m8 = "Consulte resultados en linea";
        String m9 = "Gracias por participar";

        int maxCol = 4, maxFil = 2;
        float ancho = 148, alto = 375, paso_x = 150, paso_y = 378;
        float xi = 5, yi = 792 - 10 - alto;
        int idx = 0;
        for (int f = 0; f < maxFil; f++) {
            for (int c = 0; c < maxCol; c++) {
                float x = xi + c * paso_x;
                float y = yi - f * paso_y;
                bol.drawRectangleVertical(canvas, x, y, ancho, alto, 5, 5,
                        "LA ESTRELLA", "25-06-2026", "$ 5.000",
                        m1, m2, m3, m4, m5, m6, m7, m8, m9,
                        10, 1, stmp, idx,
                        img, pre, "QR-INFO-" + idx, "QR-PREMIO-" + idx,
                        "SORT01", String.format("%06d", 1000 + idx),
                        null, null, "LA ESTRELLA", "300 000 0000", 0.35f);
                idx += 10;
            }
        }
        document.close();
        System.out.println("OK -> " + out);
    }
}
