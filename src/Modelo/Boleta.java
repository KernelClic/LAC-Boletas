/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;

public class Boleta {

    /**
     * Controla si el QR GANADOR (el del mensaje de premio) se dibuja en el
     * reporte. Por seguridad arranca en {@code false}: el QR ganador NO se
     * imprime salvo que la vista lo habilite explícitamente vía
     * {@link #setMostrarQrGanador(boolean)} tras revelar el control oculto.
     * El QR de seguridad (números) siempre se imprime.
     */
    private boolean mostrarQrGanador = false;

    /** Habilita o deshabilita la impresión del QR ganador (premio). */
    public void setMostrarQrGanador(boolean mostrar) {
        this.mostrarQrGanador = mostrar;
    }

    /** @return true si el QR ganador se imprimirá en el reporte. */
    public boolean isMostrarQrGanador() {
        return mostrarQrGanador;
    }

    public ArrayList<String> drawRectangle(PdfContentByte canvas,
            float x, float y, float ancho, float alto,
            float margen, float espacio,
            String titulo, String fecha, String valor, String msg1, String msg2, String msg3, String msg4, String msg5,
            String msg6, String msg7, String msg8, String msg9,
            int oportun,
            int colortexto,
            ArrayList<String> stmpPrint, int idx,
            Image img, Image pre, String qrInfoContent, String qrPremioContent, String codSorteo, String numeroBoleta)
            throws DocumentException, IOException {

        ArrayList<String> impresos = new ArrayList<>();

        float scaleX = ancho / 260.0f;
        float scaleY = alto / 145.0f;
        float scaleFont = Math.min(scaleX, scaleY);

        canvas.saveState();
        canvas.setGrayFill(0.9F);
        // marco externo
        canvas.rectangle(x, y, ancho, alto);
        // marco interno
        canvas.rectangle(x + 3.0F, y + 3.5F, ancho - 6.0F, alto - 7.5F);

        canvas.fillStroke();
        canvas.restoreState();

        // marco interno boleta relleno por fondo imagen
        canvas.saveState();
        img.setAbsolutePosition(x + 3.0F, y + 3.5F);
        img.scaleAbsoluteWidth(ancho - 6.0F);
        img.scaleAbsoluteHeight(alto - 7.5F);
        img.setBorder(10);
        img.setBorderColor(BaseColor.BLACK);
        canvas.addImage(img);
        canvas.restoreState();

        // Premio imagen: tope alineado con msg1 (38 desde arriba), reducida ~14%
        // bottom = y+alto-38*scaleY - 60*scaleY = y+alto-98*scaleY
        canvas.saveState();
        pre.setAbsolutePosition(x + 60.0F * scaleX, y + alto - 98.0F * scaleY);
        pre.scaleAbsoluteWidth(48 * scaleX);
        pre.scaleAbsoluteHeight(60 * scaleY);
        canvas.addImage(pre);
        canvas.restoreState();

        canvas.saveState();
        BaseFont bf = BaseFont.createFont();

        // ubicar titulo
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(1.5F);
        if (colortexto == 1) {
            canvas.setRGBColorStroke(255, 0, 0);
        }
        if (colortexto == 2) {
            canvas.setRGBColorStroke(4, 180, 4);
        }
        if (colortexto == 3) {
            canvas.setRGBColorStroke(46, 46, 254);
        }
        if (colortexto == 4) {
            canvas.setRGBColorStroke(180, 4, 174);
        }
        if (colortexto == 5) {
            canvas.setRGBColorStroke(95, 76, 11);
        }
        if (colortexto == 6) {
            canvas.setRGBColorStroke(0, 0, 0);
        }

        // Titulo
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 18.0F * scaleFont);
        canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 25.0F * scaleY);
        canvas.showText(titulo);
        canvas.endText();
        canvas.restoreState();

        /// Generar colilla - nombres y telefono
        /*
         * canvas.saveState();
         * canvas.beginText();
         * canvas.setTextRenderingMode(2);
         * canvas.setLineWidth(0.5F);
         * canvas.setRGBColorFill(0, 0, 0);
         * canvas.setFontAndSize(bf, 9.0F);
         * canvas.setTextMatrix(0,1,-1,0,x+ 20.0F, y + alto- 190.0F);
         * canvas.showText("Nombres: ____________________________");
         * canvas.endText();
         * canvas.restoreState();
         * 
         * canvas.saveState();
         * canvas.beginText();
         * canvas.setTextRenderingMode(2);
         * canvas.setLineWidth(0.5F);
         * canvas.setRGBColorFill(0, 0, 0);
         * canvas.setFontAndSize(bf, 9.0F);
         * canvas.setTextMatrix(0,1,-1,0,x+ 35.0F, y + alto- 190.0F);
         * canvas.showText("Teléfono: ____________________________");
         * canvas.endText();
         * canvas.restoreState();
         * 
         * canvas.saveState();
         * canvas.beginText();
         * canvas.setTextRenderingMode(2);
         * canvas.setLineWidth(0.5F);
         * canvas.setRGBColorStroke(255, 0, 0);
         * canvas.setRGBColorFill(255, 0, 0);
         * canvas.setFontAndSize(bf, 11.0F);
         * canvas.setTextMatrix(0,1,-1,0,x+ 50.0F, y + alto- 190.0F);
         * canvas.showText(QrString);
         * canvas.endText();
         * canvas.restoreState();
         */
        /*
         * imprimir linea vertical
         * canvas.saveState();
         * canvas.beginText();
         * canvas.setTextRenderingMode(2);
         * canvas.setLineWidth(0.3F);
         * canvas.setRGBColorFill(0, 0, 0);
         * canvas.setFontAndSize(bf, 9.0F);
         * canvas.setTextMatrix(0,1,-1,0,x+ 60.0F, y + alto- 195.0F);
         * canvas.showText("______________________________________");
         * canvas.endText();
         * canvas.restoreState();
         * // **************************************************************
         */
        // ── Bloque de textos informativos y valor ──────────────────────────────────
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5F);
        // stroke en color del texto seleccionado (aplica a todo el bloque)
        if (colortexto == 1) { canvas.setRGBColorStroke(255, 0, 0); }
        else if (colortexto == 2) { canvas.setRGBColorStroke(4, 180, 4); }
        else if (colortexto == 3) { canvas.setRGBColorStroke(46, 46, 254); }
        else if (colortexto == 4) { canvas.setRGBColorStroke(180, 4, 174); }
        else if (colortexto == 5) { canvas.setRGBColorStroke(95, 76, 11); }
        else                      { canvas.setRGBColorStroke(0, 0, 0); }

        // msg1 "El APORTE lo hace participe...": mismo renglón que fecha, a su izquierda
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 6.0F * scaleFont);
        canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 35.0F * scaleY);
        canvas.showText(msg1);

        // Línea 1: "Responsable :" (negro) + "XXX XXX XXXXX" (color del texto)
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 7.0F * scaleFont);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 50.0F * scaleY);
        canvas.showText(msg2);
        // msg3 en color seleccionado (fill = colortexto)
        if (colortexto == 1) { canvas.setRGBColorFill(255, 0, 0); }
        else if (colortexto == 2) { canvas.setRGBColorFill(4, 180, 4); }
        else if (colortexto == 3) { canvas.setRGBColorFill(46, 46, 254); }
        else if (colortexto == 4) { canvas.setRGBColorFill(180, 4, 174); }
        else if (colortexto == 5) { canvas.setRGBColorFill(95, 76, 11); }
        else                      { canvas.setRGBColorFill(0, 0, 0); }
        canvas.setTextMatrix(x + 165.0F * scaleX, y + alto - 50.0F * scaleY);
        canvas.showText(msg3);

        // Línea 2: "APORTE" (negro) + valor de la boleta (rojo) — misma línea
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 7.0F * scaleFont);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 58.0F * scaleY);
        canvas.showText(msg4);
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 8.0F * scaleFont);
        canvas.setTextMatrix(x + 142.0F * scaleX, y + alto - 58.0F * scaleY);
        canvas.showText(valor);

        // Líneas 3-6: condiciones, columna x+112, espaciado 8pt, fuente 6.5pt
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 6.5F * scaleFont);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 66.0F * scaleY);
        canvas.showText(msg5);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 74.0F * scaleY);
        canvas.showText(msg6);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 82.0F * scaleY);
        canvas.showText(msg7);
        canvas.setTextMatrix(x + 112.0F * scaleX, y + alto - 90.0F * scaleY);
        canvas.showText(msg8);

        // fecha (rojo, derecha, mismo renglón que msg1)
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 12.0F * scaleFont);
        canvas.setTextMatrix(x + 190 * scaleX, y + alto - 35.0F * scaleY);
        canvas.showText(fecha);

        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.setGrayFill(0.9F);
        /*
         * if (marco == 1) {
         * if (oportun == 1) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * if (oportun == 2) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 59.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * if (oportun == 3) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 59.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 109.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * if (oportun == 4) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 59.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 109.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 159.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * if (oportun == 5) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 59.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 109.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 159.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 209.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * if (oportun == 6) {
         * canvas.rectangle(x + 9.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 59.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 109.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 159.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 209.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * canvas.rectangle(x + 209.0F, y + alto - 137.0F, 44.0F, 18.0F);
         * }
         * 
         * 
         * }
         */

        canvas.fillStroke();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.8F);
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, Math.max(12.0F, 18.0F * scaleFont));

        if (oportun == 1) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx));
        }

        if (oportun == 2) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
        }

        if (oportun == 3) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
        }

        if (oportun == 4) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
        }

        if (oportun == 5) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 135.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
        }

        if (oportun == 6) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
        }

        if (oportun == 7) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 6));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
        }

        if (oportun == 8) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 7));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
            impresos.add(stmpPrint.get(idx + 7));
        }

        if (oportun == 9) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 8));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
            impresos.add(stmpPrint.get(idx + 7));
            impresos.add(stmpPrint.get(idx + 8));
        }

        if (oportun == 10) {
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 122.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 210.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 160.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 60.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 139.0F * scaleY);
            canvas.showText(stmpPrint.get(idx + 9));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
            impresos.add(stmpPrint.get(idx + 7));
            impresos.add(stmpPrint.get(idx + 8));
            impresos.add(stmpPrint.get(idx + 9));
        }

        canvas.endText();
        canvas.restoreState();

        // ── QR codes: dibujados AL FINAL, encima de todos los demás elementos ──
        // Contenido: QR Izquierda = mensaje del premio  |  QR Derecha = números de oportunidades
        float qrSize  = 44 * scaleFont;             // +25% respecto al tamaño original (35→44)
        // qrY: centrado vertical dentro de la zona de contenido (entre línea título y números)
        float qrY     = y + alto - 78.5f * scaleY - qrSize / 2.0f;
        float qrBgPad = 2 * scaleFont;              // padding del fondo blanco

        // QR Premio / GANADOR (IZQUIERDA) — solo se dibuja si está habilitado
        float qrPremioX = x + 8 * scaleX;
        if (mostrarQrGanador) {
            try {
                canvas.saveState();
                canvas.setColorFill(BaseColor.WHITE);
                canvas.rectangle(qrPremioX - qrBgPad, qrY - qrBgPad,
                                 qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
                canvas.fill();
                canvas.restoreState();

                Image qrPremioImg = generarQRImage(qrPremioContent, qrSize);
                qrPremioImg.setAbsolutePosition(qrPremioX, qrY);
                canvas.saveState();
                canvas.addImage(qrPremioImg);
                canvas.restoreState();
            } catch (Exception e) {
                System.err.println("[QR] Error QR Premio: " + e.getMessage());
            }
        }

        // QR Info (DERECHA) con fondo blanco — centrado en zona derecha (margen 8pt)
        float qrInfoX = x + ancho - 8 * scaleX - qrSize;
        try {
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(qrInfoX - qrBgPad, qrY - qrBgPad,
                             qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
            canvas.fill();
            canvas.restoreState();

            Image qrInfoImg = generarQRImage(qrInfoContent, qrSize);
            qrInfoImg.setAbsolutePosition(qrInfoX, qrY);
            canvas.saveState();
            canvas.addImage(qrInfoImg);
            canvas.restoreState();
        } catch (Exception e) {
            System.err.println("[QR] Error QR Info: " + e.getMessage());
        }

        return impresos;
    }

    /**
     * Dibuja un logo cuadrado: usa la imagen si viene cargada; si es null,
     * dibuja un recuadro placeholder con una letra identificadora.
     */
    private void drawLogo(PdfContentByte canvas, Image logo, float lx, float ly, float size, String letra)
            throws DocumentException, IOException {
        if (logo != null) {
            canvas.saveState();
            logo.setAbsolutePosition(lx, ly);
            logo.scaleAbsolute(size, size);
            canvas.addImage(logo);
            canvas.restoreState();
        } else {
            canvas.saveState();
            canvas.setLineWidth(0.8F);
            canvas.setRGBColorStroke(0, 0, 0);
            canvas.rectangle(lx, ly, size, size);
            canvas.stroke();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setRGBColorFill(0, 0, 0);
            canvas.setFontAndSize(BaseFont.createFont(), size * 0.55f);
            canvas.setTextMatrix(lx + size * 0.3f, ly + size * 0.28f);
            canvas.showText(letra);
            canvas.endText();
            canvas.restoreState();
        }
    }

    /**
     * Dibuja una boleta VERTICAL (reporte tipo 3): encabezado (título, fecha,
     * valor), 10 oportunidades apiladas en columna, texto de mensajes rotado al
     * costado, dos QR (GANADOR = premio, SEGURIDAD = números), logos
     * Facebook/WhatsApp (placeholder) y número de boleta al pie.
     *
     * Misma firma que {@link #drawRectangle} para que writePDF pueda
     * intercambiar el método según el tipo de reporte.
     */
    public ArrayList<String> drawRectangleVertical(PdfContentByte canvas,
            float x, float y, float ancho, float alto,
            float margen, float espacio,
            String titulo, String fecha, String valor, String msg1, String msg2, String msg3, String msg4, String msg5,
            String msg6, String msg7, String msg8, String msg9,
            int oportun,
            int colortexto,
            ArrayList<String> stmpPrint, int idx,
            Image img, Image pre, String qrInfoContent, String qrPremioContent, String codSorteo, String numeroBoleta,
            Image logoFb, Image logoWa, String textoFb, String textoWa, float wmOpacity)
            throws DocumentException, IOException {

        ArrayList<String> impresos = new ArrayList<>();

        // Referencia de diseño: 148 (ancho) x 375 (alto) — 4 boletas por fila
        float sX = ancho / 148.0f;
        float sY = alto / 375.0f;
        float sF = Math.min(sX, sY);
        float cx = x + ancho / 2.0f;          // centro horizontal de la boleta

        BaseFont bf = BaseFont.createFont();

        // ── Marco externo + interno ──────────────────────────────────────────
        canvas.saveState();
        canvas.setGrayFill(0.9F);
        canvas.rectangle(x, y, ancho, alto);
        canvas.rectangle(x + 3.0F, y + 3.5F, ancho - 6.0F, alto - 7.5F);
        canvas.fillStroke();
        canvas.restoreState();

        // ── Imagen de fondo (marca de agua) dentro del marco interno ─────────
        canvas.saveState();
        // Transparencia configurable: wmOpacity en [0,1] (1 = opaca, 0 = invisible)
        float op = Math.max(0.0f, Math.min(1.0f, wmOpacity));
        if (op < 1.0f) {
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(op);
            gs.setStrokeOpacity(op);
            canvas.setGState(gs);
        }
        img.setAbsolutePosition(x + 3.0F, y + 3.5F);
        img.scaleAbsoluteWidth(ancho - 6.0F);
        img.scaleAbsoluteHeight(alto - 7.5F);
        img.setBorder(10);
        img.setBorderColor(BaseColor.BLACK);
        canvas.addImage(img);
        canvas.restoreState();

        // ── Imagen de premio (esquina superior derecha, pequeña) ─────────────
        float preW = 30.0f * sX, preH = 32.0f * sY;
        canvas.saveState();
        pre.setAbsolutePosition(x + ancho - preW - 5.0f * sX, y + alto - preH - 5.0f * sY);
        pre.scaleAbsoluteWidth(preW);
        pre.scaleAbsoluteHeight(preH);
        canvas.addImage(pre);
        canvas.restoreState();

        // ── Encabezado CENTRADO: título, sorteo, valor ───────────────────────
        float tituloFont = 11.0F * sF;
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.7F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, tituloFont);
        canvas.setTextMatrix(cx - bf.getWidthPoint(titulo, tituloFont) / 2.0f, y + alto - 17.0F * sY);
        canvas.showText(titulo);

        float sortFont = 9.0F * sF;
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, sortFont);
        String sortTxt = "Sorteo: " + fecha;
        canvas.setTextMatrix(cx - bf.getWidthPoint(sortTxt, sortFont) / 2.0f, y + alto - 30.0F * sY);
        canvas.showText(sortTxt);

        float valFont = 8.5F * sF;
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, valFont);
        String valTxt = "Valor: " + valor;
        canvas.setTextMatrix(cx - bf.getWidthPoint(valTxt, valFont) / 2.0f, y + alto - 41.0F * sY);
        canvas.showText(valTxt);
        canvas.endText();
        canvas.restoreState();

        // ── Zonas verticales ─────────────────────────────────────────────────
        float footerH   = 14.0f * sY;                 // pie (Boleta No.)
        float msgBlockH = 104.0f * sY;                // bloque inferior (FB/WA + textos)
        float headerH   = 50.0f * sY;                 // encabezado
        float bodyTop    = y + alto - headerH;        // tope del cuerpo
        float bodyBottom = y + footerH + msgBlockH;   // base del cuerpo
        float bodyH      = bodyTop - bodyBottom;

        float margin    = 8.0f * sX;
        float leftColW  = 52.0f * sX;                  // columna de números
        float colGapXX  = 4.0f * sX;
        float leftColX  = x + margin;
        float rightColX = leftColX + leftColW + colGapXX;
        float rightColW = (x + ancho - margin) - rightColX;   // columna de QRs

        // ── 10 oportunidades (izquierda): números grandes, bien distribuidos ──
        int n = Math.min(oportun, 10);
        float slotH  = bodyH / n;
        float numFont = Math.min(13.0f * sF, slotH * 0.60f);
        for (int k = 0; k < n; k++) {
            String numero = (idx + k < stmpPrint.size()) ? stmpPrint.get(idx + k) : "####";
            float slotCenterY = bodyTop - (k + 0.5f) * slotH;

            // Recuadro tenue
            float boxH = slotH * 0.80f;
            canvas.saveState();
            canvas.setLineWidth(0.5F);
            canvas.setRGBColorStroke(120, 120, 120);
            canvas.rectangle(leftColX, slotCenterY - boxH / 2.0f, leftColW, boxH);
            canvas.stroke();
            canvas.restoreState();

            // Número centrado en el recuadro (rojo, resaltado)
            float tw = bf.getWidthPoint(numero, numFont);
            canvas.saveState();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setLineWidth(0.7F);
            canvas.setRGBColorStroke(255, 0, 0);
            canvas.setRGBColorFill(255, 0, 0);
            canvas.setFontAndSize(bf, numFont);
            canvas.setTextMatrix(leftColX + (leftColW - tw) / 2.0f, slotCenterY - numFont * 0.34f);
            canvas.showText(numero);
            canvas.endText();
            canvas.restoreState();

            impresos.add(numero);
        }

        // ── Dos QR apilados (derecha): premio arriba, seguridad abajo ────────
        // Sin etiquetas: los QR llenan la columna, limitados por el ancho de
        // columna y por la mitad del alto disponible (menos la separación).
        float qrGapV      = 8.0f * sY;                 // separación entre los dos QR
        float qrSize = Math.min(rightColW, (bodyH - qrGapV) / 2.0f);
        float qrBgPad = 2.0f * sF;
        float qrX = rightColX + (rightColW - qrSize) / 2.0f;

        // Bloque de QRs centrado verticalmente dentro del cuerpo
        float qrBlockH = 2.0f * qrSize + qrGapV;
        float qrBlockTop = bodyTop - (bodyH - qrBlockH) / 2.0f;

        float ganQrY    = qrBlockTop - qrSize;
        float segQrY    = ganQrY - qrGapV - qrSize;

        // QR GANADOR = premio — solo se dibuja si está habilitado
        if (mostrarQrGanador) {
            try {
                canvas.saveState();
                canvas.setColorFill(BaseColor.WHITE);
                canvas.rectangle(qrX - qrBgPad, ganQrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
                canvas.fill();
                canvas.restoreState();
                Image qrGan = generarQRImage(qrPremioContent, qrSize);
                qrGan.setAbsolutePosition(qrX, ganQrY);
                canvas.saveState();
                canvas.addImage(qrGan);
                canvas.restoreState();
            } catch (Exception e) {
                System.err.println("[QR] Error QR Ganador: " + e.getMessage());
            }
        }

        // QR SEGURIDAD = números de oportunidades
        try {
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(qrX - qrBgPad, segQrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
            canvas.fill();
            canvas.restoreState();
            Image qrSeg = generarQRImage(qrInfoContent, qrSize);
            qrSeg.setAbsolutePosition(qrX, segQrY);
            canvas.saveState();
            canvas.addImage(qrSeg);
            canvas.restoreState();
        } catch (Exception e) {
            System.err.println("[QR] Error QR Seguridad: " + e.getMessage());
        }

        // ── Bloque inferior CENTRADO: primero Facebook/WhatsApp, luego textos ─
        float blockTop    = bodyBottom;                 // = y + footerH + msgBlockH
        float blockBottom = y + footerH;
        float logoSize    = 13.0f * sF;
        float redesFont   = 7.5f * sF;
        float rowH        = Math.max(logoSize, redesFont) + 3.0f * sY;
        String tFb = (textoFb != null && !textoFb.trim().isEmpty()) ? textoFb : "Facebook";
        String tWa = (textoWa != null && !textoWa.trim().isEmpty()) ? textoWa : "WhatsApp";

        // Renglón Facebook (logo + texto) centrado como grupo
        float fbRowTop = blockTop - 2.0f * sY;
        float fbRowY   = fbRowTop - logoSize;
        float fbGroupW = logoSize + 4.0f * sX + bf.getWidthPoint(tFb, redesFont);
        float fbGx     = cx - fbGroupW / 2.0f;
        drawLogo(canvas, logoFb, fbGx, fbRowY, logoSize, "f");

        // Renglón WhatsApp (logo + texto) centrado como grupo
        float waRowTop = fbRowTop - rowH;
        float waRowY   = waRowTop - logoSize;
        float waGroupW = logoSize + 4.0f * sX + bf.getWidthPoint(tWa, redesFont);
        float waGx     = cx - waGroupW / 2.0f;
        drawLogo(canvas, logoWa, waGx, waRowY, logoSize, "w");

        // Textos de redes al lado de cada logo
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.4F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, redesFont);
        canvas.setTextMatrix(fbGx + logoSize + 4.0f * sX, fbRowY + (logoSize - redesFont) / 2.0f + redesFont * 0.18f);
        canvas.showText(tFb);
        canvas.setTextMatrix(waGx + logoSize + 4.0f * sX, waRowY + (logoSize - redesFont) / 2.0f + redesFont * 0.18f);
        canvas.showText(tWa);
        canvas.endText();
        canvas.restoreState();

        // Mensajes configurados en orientación NORMAL, centrados, bajo las redes
        java.util.List<String> msgs = new java.util.ArrayList<>();
        for (String m : new String[] { msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9 }) {
            if (m != null && !m.trim().isEmpty()) msgs.add(m.trim());
        }
        if (!msgs.isEmpty()) {
            float msgsTop    = waRowY - 2.0f * sY;
            float msgsBottom = blockBottom + 1.0f * sY;
            float msgsAreaH  = msgsTop - msgsBottom;
            float maxW       = ancho - 2.0f * margin;
            float lineStep   = msgsAreaH / msgs.size();
            float msgFont    = Math.min(6.0f * sF, lineStep * 0.82f);
            float widest = 0f;
            for (String m : msgs) widest = Math.max(widest, bf.getWidthPoint(m, msgFont));
            if (widest > maxW && widest > 0) msgFont *= (maxW / widest);

            canvas.saveState();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setLineWidth(0.25F);
            canvas.setRGBColorStroke(40, 40, 40);
            canvas.setRGBColorFill(40, 40, 40);
            canvas.setFontAndSize(bf, msgFont);
            for (int i = 0; i < msgs.size(); i++) {
                String m = msgs.get(i);
                float ty = msgsTop - (i + 0.75f) * lineStep;
                canvas.setTextMatrix(cx - bf.getWidthPoint(m, msgFont) / 2.0f, ty);
                canvas.showText(m);
            }
            canvas.endText();
            canvas.restoreState();
        }

        // ── Número de la boleta (pie, centrado, sobre recuadro gris) ──────────
        float footFont = 8.0F * sF;
        String foot = "Boleta No. " + numeroBoleta;
        float footW   = bf.getWidthPoint(foot, footFont);
        float footTextY = y + 5.0f * sY;               // baseline del texto
        float footPadX = 6.0f * sX;                    // margen horizontal del recuadro
        float footPadY = 3.0f * sY;                    // margen vertical del recuadro
        float footBoxX = cx - footW / 2.0f - footPadX;
        float footBoxY = footTextY - footPadY;
        float footBoxW = footW + 2.0f * footPadX;
        float footBoxH = footFont + 2.0f * footPadY;
        // Recuadro de fondo gris
        canvas.saveState();
        canvas.setRGBColorFill(210, 210, 210);
        canvas.rectangle(footBoxX, footBoxY, footBoxW, footBoxH);
        canvas.fill();
        canvas.restoreState();
        // Texto centrado sobre el recuadro
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.6F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, footFont);
        canvas.setTextMatrix(cx - footW / 2.0f, footTextY);
        canvas.showText(foot);
        canvas.endText();
        canvas.restoreState();

        return impresos;
    }

    /** Parte un texto en líneas que quepan en maxW usando la fuente/tamaño dados. */
    private java.util.List<String> wrapTexto(BaseFont bf, String texto, float font, float maxW) {
        java.util.List<String> lineas = new java.util.ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) return lineas;
        String[] palabras = texto.trim().split("\\s+");
        StringBuilder linea = new StringBuilder();
        for (String p : palabras) {
            String tentativa = (linea.length() == 0) ? p : linea + " " + p;
            if (bf.getWidthPoint(tentativa, font) <= maxW || linea.length() == 0) {
                linea.setLength(0);
                linea.append(tentativa);
            } else {
                lineas.add(linea.toString());
                linea.setLength(0);
                linea.append(p);
            }
        }
        if (linea.length() > 0) lineas.add(linea.toString());
        return lineas;
    }

    /**
     * Dibuja una línea de texto centrada en cx, en la baseline y, reduciendo la
     * fuente si el texto no cabe en maxW. Devuelve la y de la baseline usada.
     * (r,g,b) es el color; 0,0,0 = negro.
     */
    private float drawCentradoAutofit(PdfContentByte canvas, BaseFont bf, String texto,
            float font, float cx, float y, float maxW, int r, int g, int b)
            throws DocumentException, IOException {
        if (texto == null || texto.isEmpty()) return y;
        float f = font;
        float w = bf.getWidthPoint(texto, f);
        if (w > maxW && w > 0) { f *= (maxW / w); w = maxW; }
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.4F);
        canvas.setRGBColorStroke(r, g, b);
        canvas.setRGBColorFill(r, g, b);
        canvas.setFontAndSize(bf, f);
        canvas.setTextMatrix(cx - w / 2.0f, y);
        canvas.showText(texto);
        canvas.endText();
        canvas.restoreState();
        return y - f;
    }

    /**
     * Dibuja un mensaje (posiblemente multilínea, con wrap) alineado a la
     * izquierda desde x0, comenzando en la baseline y, con color (r,g,b).
     * Devuelve la y de la siguiente línea disponible. Si el texto es vacío,
     * devuelve y sin cambios.
     */
    private float drawBloqueIzq(PdfContentByte canvas, BaseFont bf, String texto,
            float font, float x0, float y, float maxW, int r, int g, int b)
            throws DocumentException, IOException {
        if (texto == null || texto.trim().isEmpty()) return y;
        java.util.List<String> lineas = wrapTexto(bf, texto, font, maxW);
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.4F);
        canvas.setRGBColorStroke(r, g, b);
        canvas.setRGBColorFill(r, g, b);
        canvas.setFontAndSize(bf, font);
        float cy = y;
        for (String l : lineas) {
            canvas.setTextMatrix(x0, cy);
            canvas.showText(l);
            cy -= (font + 2.0f);
        }
        canvas.endText();
        canvas.restoreState();
        return cy;
    }

    /**
     * Dibuja un mensaje JUSTIFICADO (flush a ambos márgenes) desde x0, ocupando
     * exactamente el ancho w. Cada línea se estira con espaciado de caracteres
     * para tocar el margen izquierdo y el derecho. La fuente se reduce si algún
     * token no cabe en w. Devuelve la y de la siguiente línea disponible.
     */
    private float drawBloqueJustificado(PdfContentByte canvas, BaseFont bf, String texto,
            float font, float x0, float y, float w, int r, int g, int b, float lineGap)
            throws DocumentException, IOException {
        if (texto == null || texto.trim().isEmpty()) return y;
        // Reduce la fuente si el token más ancho no cabe en w.
        float f = font;
        for (String p : texto.trim().split("\\s+")) {
            float pw = bf.getWidthPoint(p, f);
            if (pw > w && pw > 0) f *= (w / pw);
        }
        java.util.List<String> lineas = wrapTexto(bf, texto, f, w);
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.4F);
        canvas.setRGBColorStroke(r, g, b);
        canvas.setRGBColorFill(r, g, b);
        canvas.setFontAndSize(bf, f);
        float cy = y;
        for (String l : lineas) {
            float lw = bf.getWidthPoint(l, f);
            int n = l.length();
            // Espaciado extra por carácter para que la línea llene w (flush izq/der).
            float cs = (n > 1 && lw < w) ? (w - lw) / (n - 1) : 0f;
            canvas.setCharacterSpacing(cs);
            canvas.setTextMatrix(x0, cy);
            canvas.showText(l);
            cy -= (f + lineGap);
        }
        canvas.setCharacterSpacing(0f);
        canvas.endText();
        canvas.restoreState();
        return cy;
    }

    /** Dibuja una columna (cuadrante) de números apilados, negros y en negrita. */
    private void drawCuadranteNumeros(PdfContentByte canvas, BaseFont bf,
            ArrayList<String> stmpPrint, int startIdx, int count,
            float colX, float colW, float firstCenterY, float step, float numFont,
            ArrayList<String> impresos) throws DocumentException, IOException {
        // Ajusta la fuente si el número más ancho no cabe en la columna
        float maxW = colW * 0.92f;
        float fuente = numFont;
        for (int k = 0; k < count; k++) {
            String s = (startIdx + k < stmpPrint.size()) ? stmpPrint.get(startIdx + k) : "#####";
            float w = bf.getWidthPoint(s, fuente);
            if (w > maxW && w > 0) fuente *= (maxW / w);
        }
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);          // fill + stroke = negrita
        canvas.setLineWidth(0.6F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, fuente);
        for (int k = 0; k < count; k++) {
            String numero = (startIdx + k < stmpPrint.size()) ? stmpPrint.get(startIdx + k) : "#####";
            float tw = bf.getWidthPoint(numero, fuente);
            float ty = firstCenterY - k * step - fuente * 0.34f;
            canvas.setTextMatrix(colX + (colW - tw) / 2.0f, ty);
            canvas.showText(numero);
            impresos.add(numero);
        }
        canvas.endText();
        canvas.restoreState();
    }

    /**
     * Dibuja una boleta de CUADRANTES (reporte tipo 4): 20 oportunidades en
     * cuatro cuadros (2×2), columna central con título/caducidad/valor arriba,
     * banda central con aviso legal + redes (WhatsApp/Facebook), y zona inferior
     * central con texto vertical, imagen de dinero y dos QR (GANADOR/SEGURIDAD).
     *
     * Misma firma que {@link #drawRectangleVertical} para que writePDF pueda
     * intercambiar el método según el tipo de reporte.
     *
     * Mapa de mensajes (según componentes de la interfaz):
     *   msg1 = Caducidad (p.ej. "CADUCIDAD 10 AM")
     *   msg2 = Aviso línea 1 (p.ej. "TACHONES-BORRONES ENMENDADURAS")
     *   msg3 = Aviso línea 2 (p.ej. "ALTERACIONES")
     *   msg4 = Aviso resaltado en ROJO (p.ej. "SE ANULA EL BOLETO")
     *   msg5 = Texto vertical inferior (p.ej. "SOMOS FUENTE DE EMPLEO")
     */
    public ArrayList<String> drawRectangleCuadrantes(PdfContentByte canvas,
            float x, float y, float ancho, float alto,
            float margen, float espacio,
            String titulo, String fecha, String valor, String msg1, String msg2, String msg3, String msg4, String msg5,
            String msg6, String msg7, String msg8, String msg9,
            int oportun,
            int colortexto,
            ArrayList<String> stmpPrint, int idx,
            Image img, Image pre, String qrInfoContent, String qrPremioContent, String codSorteo, String numeroBoleta,
            Image logoFb, Image logoWa, String textoFb, String textoWa, float wmOpacity)
            throws DocumentException, IOException {

        ArrayList<String> impresos = new ArrayList<>();

        // Referencia de diseño: 148 (ancho) x 375 (alto) — 4 boletas por fila
        float sX = ancho / 148.0f;
        float sY = alto / 375.0f;
        float sF = Math.min(sX, sY);
        float cx = x + ancho / 2.0f;

        BaseFont bf = BaseFont.createFont();

        // ── Marco externo + interno ──────────────────────────────────────────
        canvas.saveState();
        canvas.setGrayFill(0.9F);
        canvas.rectangle(x, y, ancho, alto);
        canvas.rectangle(x + 3.0F, y + 3.5F, ancho - 6.0F, alto - 7.5F);
        canvas.fillStroke();
        canvas.restoreState();

        // ── Imagen de fondo (marca de agua) con opacidad configurable ────────
        canvas.saveState();
        float op = Math.max(0.0f, Math.min(1.0f, wmOpacity));
        if (op < 1.0f) {
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(op);
            gs.setStrokeOpacity(op);
            canvas.setGState(gs);
        }
        img.setAbsolutePosition(x + 3.0F, y + 3.5F);
        img.scaleAbsoluteWidth(ancho - 6.0F);
        img.scaleAbsoluteHeight(alto - 7.5F);
        img.setBorder(10);
        img.setBorderColor(BaseColor.BLACK);
        canvas.addImage(img);
        canvas.restoreState();

        // ── Geometría: cuadros con MARGEN respecto al marco de la boleta ─────
        float frameInsetX = 10.0f * sX;                  // margen horizontal al marco
        float frameInsetY = 12.0f * sY;                  // margen vertical al marco
        float boxW    = 44.0f * sX;
        float leftX   = x + frameInsetX;                 // margen a la izquierda
        float rightX  = x + ancho - frameInsetX - boxW;  // margen a la derecha
        float centerL = leftX + boxW;
        float centerR = rightX;
        float centerGap = 3.0f * sX;
        float centerW = (centerR - centerL) - 2.0f * centerGap;   // ancho útil para textos
        float centerCx = (centerL + centerR) / 2.0f;

        // Cuadros en las esquinas, separados del marco por el margen. Los
        // inferiores dejan una franja para el serial. Cuadros MÁS ALTOS: los 5
        // números de cada cuadrante se re-espacian uniformemente (slot = boxH/5).
        float boxH = 137.0f * sY;
        float topBoxTop = y + alto - frameInsetY;        // margen arriba
        float topBoxBot = topBoxTop - boxH;
        float botBoxBot = y + frameInsetY + 4.0f * sY;   // margen abajo + franja serial
        float botBoxTop = botBoxBot + boxH;
        canvas.saveState();
        canvas.setColorFill(BaseColor.WHITE);      // fondo BLANCO dentro de los cuadros
        canvas.setLineWidth(0.9F);
        canvas.setRGBColorStroke(20, 20, 20);
        canvas.rectangle(leftX,  topBoxBot, boxW, boxH);   // TL
        canvas.rectangle(rightX, topBoxBot, boxW, boxH);   // TR
        canvas.rectangle(leftX,  botBoxBot, boxW, boxH);   // BL
        canvas.rectangle(rightX, botBoxBot, boxW, boxH);   // BR
        canvas.fillStroke();
        canvas.restoreState();

        // ── 20 números en 4 cuadrantes (TL, TR, BL, BR = 5 c/u) ──────────────
        // Distribución pareja dentro de cada cuadro; fuente GRANDE (autoajustada).
        int nOp = Math.min(oportun, 20);
        int porCuadrante = 5;
        float slot = boxH / porCuadrante;
        float numFont = Math.min(slot * 0.86f, boxW * 0.40f);
        float topFirstY = topBoxTop - slot / 2.0f;   // centro del 1er número (arriba)
        float botFirstY = botBoxTop - slot / 2.0f;   // centro del 1er número (abajo)
        // TL
        drawCuadranteNumeros(canvas, bf, stmpPrint, idx + 0,  Math.min(porCuadrante, Math.max(0, nOp - 0)),
                leftX,  boxW, topFirstY, slot, numFont, impresos);
        // TR
        drawCuadranteNumeros(canvas, bf, stmpPrint, idx + 5,  Math.min(porCuadrante, Math.max(0, nOp - 5)),
                rightX, boxW, topFirstY, slot, numFont, impresos);
        // BL
        drawCuadranteNumeros(canvas, bf, stmpPrint, idx + 10, Math.min(porCuadrante, Math.max(0, nOp - 10)),
                leftX,  boxW, botFirstY, slot, numFont, impresos);
        // BR
        drawCuadranteNumeros(canvas, bf, stmpPrint, idx + 15, Math.min(porCuadrante, Math.max(0, nOp - 15)),
                rightX, boxW, botFirstY, slot, numFont, impresos);

        // ── Columna central superior: título (grande), caducidad (grande), valor ─
        // Todo centrado y ACOTADO a centerW (autoajuste) para no invadir cuadros.
        float titFont = 13.0f * sF;
        java.util.List<String> titLineas = wrapTexto(bf, titulo, titFont, centerW);
        float ty = y + alto - 24.0f * sY;   // título más abajo
        for (String l : titLineas) {
            ty = drawCentradoAutofit(canvas, bf, l, titFont, centerCx, ty, centerW, 0, 0, 0);
            ty -= 2.5f * sY;
        }

        // Caducidad (msg1) — MÁS GRANDE y en DOS líneas centradas
        // ("CADUCIDAD" / "10 AM"). Fuente común: la mayor que haga caber la línea
        // más ancha en el ancho central.
        float cadFont = 13.0f * sF;
        ty -= 9.0f * sY;
        if (msg1 != null && !msg1.trim().isEmpty()) {
            java.util.List<String> cadL = wrapTexto(bf, msg1.trim(), cadFont, centerW);
            float fCad = cadFont;
            for (String l : cadL) {
                float w = bf.getWidthPoint(l, fCad);
                if (w > centerW && w > 0) fCad *= centerW / w;
            }
            for (String l : cadL) {
                ty = drawCentradoAutofit(canvas, bf, l, fCad, centerCx, ty, centerW, 0, 0, 0);
                ty -= 2.5f * sY;
            }
        }

        // Valor — MÁS GRANDE y en DOS líneas centradas ("VALOR" / "20 PESOS").
        // FLUYE justo debajo de la caducidad (no se ancla desde abajo) para no
        // solaparse con ella.
        String valTxt = (valor != null && !valor.trim().isEmpty()) ? valor.trim() : "";
        if (!valTxt.isEmpty()) {
            // Forzar 2 líneas: "VALOR" arriba y el monto ("20 PESOS") junto abajo.
            String monto = valTxt.toUpperCase().startsWith("VALOR")
                    ? valTxt.substring(5).trim() : valTxt;
            java.util.List<String> valL = new java.util.ArrayList<>();
            valL.add("VALOR");
            if (!monto.isEmpty()) valL.add(monto);
            float valFont = 13.0f * sF;
            float fVal = valFont;
            for (String l : valL) {
                float w = bf.getWidthPoint(l, fVal);
                if (w > centerW && w > 0) fVal *= centerW / w;
            }
            ty -= 7.0f * sY;   // separación respecto a la caducidad
            for (String l : valL) {
                ty = drawCentradoAutofit(canvas, bf, l, fVal, centerCx, ty, centerW, 0, 0, 0);
                ty -= 2.5f * sY;
            }
        }

        // ── Banda central: aviso (izq) + redes (der) ─────────────────────────
        // msg2 = "El aporte..." (grande, un poco menos que el título)
        // msg3 = "Responsable..." (mediano)  ·  msg4 = aviso en ROJO
        float bandTop = topBoxBot - 4.0f * sY;
        float avisoX = leftX;                            // margen izq. = borde de los cuadros
        float avisoRight = cx - 4.0f * sX;               // no invade la zona central/redes
        float avisoW = avisoRight - avisoX;              // ancho útil del bloque de aviso
        float aporteFont = 10.5f * sF;
        float respFont   = 9.5f * sF;
        float rojoFont   = 8.8f * sF;   // cabe "SE ANULA EL BOLETO" en una sola línea
        float lineGap    = 2.5f * sY;
        float ay = bandTop - aporteFont;
        // msg2 (aviso negro, p.ej. "TACHONES-BORRONES ENMENDADURAS ALTERACIONES")
        // JUSTIFICADO al ancho de los cuadros (flush izq/der), sin salirse del margen.
        ay = drawBloqueJustificado(canvas, bf, msg2, aporteFont, avisoX, ay, avisoW, 0, 0, 0, lineGap);
        // msg3 (línea extra opcional) — también justificada
        ay = drawBloqueJustificado(canvas, bf, msg3, respFont, avisoX, ay, avisoW, 0, 0, 0, lineGap);
        // msg4 — ROJO, justificado ("SE ANULA EL BOLETO")
        ay = drawBloqueJustificado(canvas, bf, msg4, rojoFont, avisoX, ay, avisoW, 200, 0, 0, lineGap);

        // Redes (WhatsApp + Facebook) — logos y textos un poco más grandes y
        // desplazados un poco a la izquierda.
        float logoSize = 20.5f * sF;
        float redesFont = 10.5f * sF;
        float redesX = cx - 1.0f * sX;
        String tWa = (textoWa != null && !textoWa.trim().isEmpty()) ? textoWa : "WhatsApp";
        String tFb = (textoFb != null && !textoFb.trim().isEmpty()) ? textoFb : "Facebook";
        float waRowY = bandTop - logoSize;
        float fbRowY = waRowY - logoSize - 6.0f * sY;
        drawLogo(canvas, logoWa, redesX, waRowY, logoSize, "w");
        drawLogo(canvas, logoFb, redesX, fbRowY, logoSize, "f");
        // Texto junto a los logos, autoajustado para NO traslapar el borde derecho
        // de la boleta (cada texto reduce su fuente sólo si no cabe).
        float redesTxtX = redesX + logoSize + 4.0f * sX;
        float redesTxtMaxW = (x + ancho - frameInsetX) - redesTxtX;
        float waFont = redesFont;
        float waW = bf.getWidthPoint(tWa, waFont);
        if (waW > redesTxtMaxW && waW > 0) waFont *= redesTxtMaxW / waW;
        float fbFont = redesFont;
        float fbW = bf.getWidthPoint(tFb, fbFont);
        if (fbW > redesTxtMaxW && fbW > 0) fbFont *= redesTxtMaxW / fbW;
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.35F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, waFont);
        canvas.setTextMatrix(redesTxtX, waRowY + (logoSize - waFont) / 2.0f + waFont * 0.18f);
        canvas.showText(tWa);
        canvas.setFontAndSize(bf, fbFont);
        canvas.setTextMatrix(redesTxtX, fbRowY + (logoSize - fbFont) / 2.0f + fbFont * 0.18f);
        canvas.showText(tFb);
        canvas.endText();
        canvas.restoreState();

        // "SOMOS FUENTE DE EMPLEO" (msg5): franja de ANCHO COMPLETO (del extremo
        // izquierdo al derecho de la boleta), CENTRADA, en naranja, al pie de la banda.
        if (msg5 != null && !msg5.trim().isEmpty()) {
            float somosFont = 10.5f * sF;
            float somosLeft = leftX;
            float somosRight = rightX + boxW;
            float somosW = somosRight - somosLeft;
            float somosCx = (somosLeft + somosRight) / 2.0f;
            float somosY = botBoxTop + 4.5f * sY;
            drawCentradoAutofit(canvas, bf, msg5.trim(), somosFont, somosCx, somosY, somosW, 200, 60, 0);
        }

        // ── Zona inferior central: QR(s) lo más GRANDES posible ──────────────
        // Ocupan el hueco entre los cuadros inferiores; el ancho del hueco es el
        // límite (no invaden los cuadros de números). El texto vertical se movió
        // debajo de Facebook, así que aquí ya no estorba nada.
        float qrAreaTop = botBoxTop - 2.0f * sY;      // justo bajo la banda
        float qrAreaBot = botBoxBot + 2.0f * sY;
        float qrMaxW = (centerR - centerL) - 2.0f * sX;   // QR un poco más anchos
        float qrBgPad = 0.6f * sF;                         // borde blanco mínimo
        float qrX, ganQrY = 0f, segQrY;
        float qrSize;
        if (mostrarQrGanador) {
            // Dos QR apilados y centrados verticalmente en la zona.
            float qrGapV = 5.0f * sY;
            qrSize = Math.min((qrAreaTop - qrAreaBot - qrGapV) / 2.0f, qrMaxW);
            qrX = centerCx - qrSize / 2.0f;
            float qrBlockH = 2.0f * qrSize + qrGapV;
            float qrBlockTop = qrAreaTop - ((qrAreaTop - qrAreaBot) - qrBlockH) / 2.0f;
            ganQrY = qrBlockTop - qrSize;
            segQrY = ganQrY - qrGapV - qrSize;
        } else {
            // Un solo QR (seguridad): el mayor que quepa, centrado.
            qrSize = Math.min(qrAreaTop - qrAreaBot, qrMaxW);
            qrX = centerCx - qrSize / 2.0f;
            segQrY = (qrAreaTop + qrAreaBot) / 2.0f - qrSize / 2.0f;
        }
        if (mostrarQrGanador) {
            try {
                canvas.saveState();
                canvas.setColorFill(BaseColor.WHITE);
                canvas.rectangle(qrX - qrBgPad, ganQrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
                canvas.fill();
                canvas.restoreState();
                Image qrGan = generarQRImage(qrPremioContent, qrSize);
                qrGan.setAbsolutePosition(qrX, ganQrY);
                canvas.saveState();
                canvas.addImage(qrGan);
                canvas.restoreState();
            } catch (Exception e) {
                System.err.println("[QR] Error QR Ganador (cuadrantes): " + e.getMessage());
            }
        }
        try {
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(qrX - qrBgPad, segQrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
            canvas.fill();
            canvas.restoreState();
            Image qrSeg = generarQRImage(qrInfoContent, qrSize);
            qrSeg.setAbsolutePosition(qrX, segQrY);
            canvas.saveState();
            canvas.addImage(qrSeg);
            canvas.restoreState();
        } catch (Exception e) {
            System.err.println("[QR] Error QR Seguridad (cuadrantes): " + e.getMessage());
        }

        // ── Número de la boleta (pie, esquina inferior izquierda) ────────────
        float footFont = 7.0F * sF;
        String foot = numeroBoleta;
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, footFont);
        canvas.setTextMatrix(x + 7.0f * sX, y + 6.0f * sY);
        canvas.showText(foot);
        canvas.endText();
        canvas.restoreState();

        return impresos;
    }

    /**
     * Genera un QR code usando ZXing a 200×200px nativos, exportado como PNG
     * y cargado como imagen iText. Esto garantiza módulos nítidos al imprimir.
     *
     * @param content    texto a codificar
     * @param sizePoints tamaño en puntos PDF del QR en el documento
     */
    private static Image generarQRImage(String content, float sizePoints) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);          // quiet zone: 2 módulos
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter writer = new QRCodeWriter();
        int px = 200; // resolución nativa: 200×200px
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, px, px, hints);

        // Convertir BitMatrix → BufferedImage RGB
        BufferedImage bimg = new BufferedImage(px, px, BufferedImage.TYPE_INT_RGB);
        for (int xi = 0; xi < px; xi++) {
            for (int yi = 0; yi < px; yi++) {
                bimg.setRGB(xi, yi, matrix.get(xi, yi) ? 0x000000 : 0xFFFFFF);
            }
        }

        // Exportar como PNG en memoria → iText Image (evita interpolación)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bimg, "PNG", baos);
        Image img = Image.getInstance(baos.toByteArray());
        img.setInterpolation(false);          // escalar sin suavizado = módulos nítidos
        img.scaleAbsolute(sizePoints, sizePoints);
        return img;
    }

    public void nrosFaltantes(PdfContentByte canvas, ArrayList<String> nFatantes)
            throws DocumentException, IOException {

        canvas.saveState();
        BaseFont bf = BaseFont.createFont();

        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(1.5F);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 16.0F);
        canvas.setTextMatrix(50.0F, 680F);
        canvas.showText("Números Faltantes");
        canvas.endText();
        canvas.restoreState();

        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.8F);
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 22.0F);

        Iterator<String> idx = nFatantes.iterator();

        int i = 1;
        int mnum = 10;
        int cad = 0;
        String prncad = "";

        canvas.setTextMatrix(10.0F, 640.0F);

        while (idx.hasNext()) {
            String elemento = idx.next();
            prncad = prncad + elemento + " ";

            if (i % mnum == 0) {
                cad++;
                switch (cad) {
                    case 1:
                        canvas.setTextMatrix(10.0F, 640.0F);
                        break;
                    case 2:
                        canvas.setTextMatrix(10.0F, 620.0F);
                        break;
                    case 3:
                        canvas.setTextMatrix(10.0F, 600.0F);
                        break;
                    case 4:
                        canvas.setTextMatrix(10.0F, 580.0F);
                        break;
                    case 5:
                        canvas.setTextMatrix(10.0F, 560.0F);
                        break;
                    case 6:
                        canvas.setTextMatrix(10.0F, 540.0F);
                        break;
                    case 7:
                        canvas.setTextMatrix(10.0F, 520.0F);
                        break;
                    case 8:
                        canvas.setTextMatrix(10.0F, 500.0F);
                        break;
                    case 9:
                        canvas.setTextMatrix(10.0F, 480.0F);
                        break;
                    case 10:
                        canvas.setTextMatrix(10.0F, 460.0F);
                        break;
                    case 11:
                        canvas.setTextMatrix(10.0F, 440.0F);
                        break;
                }
                canvas.showText(prncad);
                prncad = "";
            }
            i++;
        }

        if (i - 1 < 9) {
            canvas.showText(prncad);
        }

        canvas.endText();
        canvas.restoreState();
    }

}
