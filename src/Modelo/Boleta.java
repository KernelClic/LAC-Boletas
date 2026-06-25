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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;

public class Boleta {

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

        // QR Premio (IZQUIERDA) con fondo blanco — centrado en zona izquierda (margen 8pt)
        float qrPremioX = x + 8 * scaleX;
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
            Image logoFb, Image logoWa, String textoFb, String textoWa)
            throws DocumentException, IOException {

        ArrayList<String> impresos = new ArrayList<>();

        // Referencia de diseño: 196 (ancho) x 375 (alto)
        float sX = ancho / 196.0f;
        float sY = alto / 375.0f;
        float sF = Math.min(sX, sY);

        BaseFont bf = BaseFont.createFont();

        // ── Marco externo + interno ──────────────────────────────────────────
        canvas.saveState();
        canvas.setGrayFill(0.9F);
        canvas.rectangle(x, y, ancho, alto);
        canvas.rectangle(x + 3.0F, y + 3.5F, ancho - 6.0F, alto - 7.5F);
        canvas.fillStroke();
        canvas.restoreState();

        // ── Imagen de fondo dentro del marco interno ─────────────────────────
        canvas.saveState();
        img.setAbsolutePosition(x + 3.0F, y + 3.5F);
        img.scaleAbsoluteWidth(ancho - 6.0F);
        img.scaleAbsoluteHeight(alto - 7.5F);
        img.setBorder(10);
        img.setBorderColor(BaseColor.BLACK);
        canvas.addImage(img);
        canvas.restoreState();

        // ── Imagen de premio (esquina superior derecha, pequeña) ─────────────
        canvas.saveState();
        pre.setAbsolutePosition(x + ancho - 50.0F * sX, y + alto - 56.0F * sY);
        pre.scaleAbsoluteWidth(40 * sX);
        pre.scaleAbsoluteHeight(44 * sY);
        canvas.addImage(pre);
        canvas.restoreState();

        // ── Encabezado: título, fecha de sorteo, valor ───────────────────────
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.7F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 12.0F * sF);
        canvas.setTextMatrix(x + 10.0F * sX, y + alto - 20.0F * sY);
        canvas.showText(titulo);

        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 9.5F * sF);
        canvas.setTextMatrix(x + 10.0F * sX, y + alto - 33.0F * sY);
        canvas.showText("Sorteo: " + fecha);

        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 9.0F * sF);
        canvas.setTextMatrix(x + 10.0F * sX, y + alto - 45.0F * sY);
        canvas.showText("Valor: " + valor);
        canvas.endText();
        canvas.restoreState();

        // ── 10 oportunidades apiladas: solo el número, dentro de su marco ────
        // Mismo tratamiento que los otros reportes: número en ROJO, resaltado
        // (modo 2). Sin etiqueta "Oportunidad N"; cada número va en su recuadro.
        int n = Math.min(oportun, 10);
        float opTop  = 56.0f;    // tope del 1er recuadro (desde el borde superior)
        float boxH   = 10.5f;    // alto de cada recuadro
        float opStep = 12.4f;    // separación vertical entre recuadros
        float boxLeft = x + 10.0f * sX;
        float boxW    = 74.0f * sX;
        float numFont = 10.5f * sF;
        for (int k = 0; k < n; k++) {
            float boxY = y + alto - (opTop + k * opStep + boxH) * sY;   // esquina inferior del marco
            String numero = (idx + k < stmpPrint.size()) ? stmpPrint.get(idx + k) : "####";

            // Marco de la oportunidad
            canvas.saveState();
            canvas.setLineWidth(0.5F);
            canvas.setRGBColorStroke(120, 120, 120);
            canvas.rectangle(boxLeft, boxY, boxW, boxH * sY);
            canvas.stroke();
            canvas.restoreState();

            // Número centrado en el marco (rojo, resaltado) — fuente como otros reportes
            float tw = bf.getWidthPoint(numero, numFont);
            float tx = boxLeft + (boxW - tw) / 2.0f;
            float tyb = boxY + (boxH * sY - numFont) / 2.0f + numFont * 0.18f;
            canvas.saveState();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setLineWidth(0.7F);
            canvas.setRGBColorStroke(255, 0, 0);
            canvas.setRGBColorFill(255, 0, 0);
            canvas.setFontAndSize(bf, numFont);
            canvas.setTextMatrix(tx, tyb);
            canvas.showText(numero);
            canvas.endText();
            canvas.restoreState();

            impresos.add(numero);
        }

        // ── Mensajes configurados como columnas verticales (rotadas 90°),
        //    distribuidas en el espacio libre a la derecha sin traslaparse ──
        java.util.List<String> msgs = new java.util.ArrayList<>();
        for (String m : new String[] { msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9 }) {
            if (m != null && !m.trim().isEmpty()) msgs.add(m.trim());
        }
        if (!msgs.isEmpty()) {
            float bandRight = x + ancho - 7.0f * sX;    // borde derecho del área de columnas
            float bandLeft  = x + 90.0f * sX;            // a la derecha de los recuadros
            float bandW     = bandRight - bandLeft;
            float colYbot   = y + alto - 178.0f * sY;    // base (abajo) de las columnas
            float colYtop   = y + alto - 58.0f * sY;     // tope (arriba, bajo la imagen de premio)
            float colH      = colYtop - colYbot;         // alto disponible para cada columna
            float colGap    = bandW / msgs.size();       // separación horizontal entre columnas

            // Fuente: que las columnas no se traslapen y que el texto quepa en el alto.
            float msgFont = Math.min(6.0f * sF, colGap * 0.75f);
            float maxTextW = 0f;
            for (String m : msgs) maxTextW = Math.max(maxTextW, bf.getWidthPoint(m, msgFont));
            if (maxTextW > colH && maxTextW > 0) msgFont *= (colH / maxTextW);

            canvas.saveState();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setLineWidth(0.25F);
            canvas.setRGBColorStroke(60, 60, 60);
            canvas.setRGBColorFill(60, 60, 60);
            canvas.setFontAndSize(bf, msgFont);
            // matriz de rotación 90° CCW: cada columna lee de abajo hacia arriba.
            // px = borde derecho de la columna; el cuerpo del texto crece hacia la izquierda.
            for (int i = 0; i < msgs.size(); i++) {
                float px = bandRight - (i + 0.5f) * colGap;
                canvas.setTextMatrix(0, 1, -1, 0, px, colYbot);
                canvas.showText(msgs.get(i));
            }
            canvas.endText();
            canvas.restoreState();
        }

        // ── Dos QR: GANADOR (premio, izq) y SEGURIDAD (números, der) ─────────
        float qrSize = 50.0f * sF;
        float qrBgPad = 2.0f * sF;
        float qrTopY = y + alto - 190.0f * sY;          // borde superior del QR
        float qrY = qrTopY - qrSize;                     // esquina inferior izq
        float qrGanX = x + 14.0f * sX;
        float qrSegX = x + ancho - 14.0f * sX - qrSize;

        // Etiquetas
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 6.5F * sF);
        canvas.setTextMatrix(qrGanX, qrTopY + 3.0f * sY);
        canvas.showText("QR GANADOR");
        canvas.setTextMatrix(qrSegX, qrTopY + 3.0f * sY);
        canvas.showText("QR SEGURIDAD");
        canvas.endText();
        canvas.restoreState();

        // QR GANADOR = premio
        try {
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(qrGanX - qrBgPad, qrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
            canvas.fill();
            canvas.restoreState();
            Image qrGan = generarQRImage(qrPremioContent, qrSize);
            qrGan.setAbsolutePosition(qrGanX, qrY);
            canvas.saveState();
            canvas.addImage(qrGan);
            canvas.restoreState();
        } catch (Exception e) {
            System.err.println("[QR] Error QR Ganador: " + e.getMessage());
        }

        // QR SEGURIDAD = números de oportunidades
        try {
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(qrSegX - qrBgPad, qrY - qrBgPad, qrSize + 2 * qrBgPad, qrSize + 2 * qrBgPad);
            canvas.fill();
            canvas.restoreState();
            Image qrSeg = generarQRImage(qrInfoContent, qrSize);
            qrSeg.setAbsolutePosition(qrSegX, qrY);
            canvas.saveState();
            canvas.addImage(qrSeg);
            canvas.restoreState();
        } catch (Exception e) {
            System.err.println("[QR] Error QR Seguridad: " + e.getMessage());
        }

        // ── Logos Facebook / WhatsApp en dos renglones (logo + texto) ────────
        float logoSize = 16.0f * sF;
        float fbRowY = qrY - 14.0f * sY - logoSize;      // 1er renglón: Facebook
        float waRowY = fbRowY - 20.0f * sY;              // 2º renglón: WhatsApp
        float logoX = x + 14.0f * sX;
        float txtX = logoX + logoSize + 5.0f * sX;

        // Logos (imagen real o recuadro placeholder)
        drawLogo(canvas, logoFb, logoX, fbRowY, logoSize, "f");
        drawLogo(canvas, logoWa, logoX, waRowY, logoSize, "w");

        // Textos al lado de cada logo
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.4F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 7.0F * sF);
        canvas.setTextMatrix(txtX, fbRowY + logoSize / 2.0f - 3.0f * sY);
        canvas.showText((textoFb != null && !textoFb.trim().isEmpty()) ? textoFb : "Facebook");
        canvas.setTextMatrix(txtX, waRowY + logoSize / 2.0f - 3.0f * sY);
        canvas.showText((textoWa != null && !textoWa.trim().isEmpty()) ? textoWa : "WhatsApp");
        canvas.endText();
        canvas.restoreState();

        // ── Número de la boleta (pie) ────────────────────────────────────────
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.6F);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 8.0F * sF);
        canvas.setTextMatrix(x + 12.0f * sX, y + 9.0f * sY);
        canvas.showText("Boleta No. " + numeroBoleta);
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
