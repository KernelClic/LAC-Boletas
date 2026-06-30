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
