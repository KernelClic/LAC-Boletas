/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Boleta {

    public ArrayList<String> drawRectangle(PdfContentByte canvas,
            float x, float y, float ancho, float alto,
            float margen, float espacio,
            String titulo, String fecha, String valor, String msg1, String msg2, String msg3, String msg4, String msg5,
            String msg6, String msg7, String msg8, String msg9,
            int oportun,
            int colortexto,
            ArrayList<String> stmpPrint, int idx,
            Image img, Image pre, String uuidAPI, String codSorteo, String numeroBoleta)
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

        // marco interno boleta relleno por Premio imagen
        canvas.saveState();
        pre.setAbsolutePosition(x + 10.0F * scaleX, y + alto - 110.0F * scaleY);
        pre.scaleAbsoluteWidth(99 * scaleX);
        pre.scaleAbsoluteHeight(80 * scaleY);
        canvas.addImage(pre);
        canvas.restoreState();

        BarcodeQRCode my_code = new BarcodeQRCode(
                "https://poco.absapex.net/apex/api_boletas/api_boletas/v1/qr/consultar/" + uuidAPI, 1, 1, null);
        Image QRimage = my_code.getImage();
        canvas.saveState();
        QRimage.setAbsolutePosition(x + ancho - 48 * scaleX, y + alto - 71 * scaleY);
        QRimage.scaleAbsoluteWidth(36 * scaleFont);
        QRimage.scaleAbsoluteHeight(36 * scaleFont);
        QRimage.setBorderColor(BaseColor.BLACK);
        canvas.addImage(QRimage);
        canvas.restoreState();

        // Imprimir código de sorteo y boleta debajo del QR
        if (codSorteo != null && !codSorteo.isEmpty()) {
            canvas.saveState();
            BaseFont bfSorteo = BaseFont.createFont();
            canvas.beginText();
            canvas.setTextRenderingMode(2);
            canvas.setLineWidth(0.5F);
            canvas.setRGBColorStroke(0, 0, 0);
            canvas.setRGBColorFill(0, 0, 0);
            canvas.setFontAndSize(bfSorteo, 6.0F * scaleFont);
            // Sorteo en la posición original
            canvas.setTextMatrix(x + ancho - 45 * scaleX, y + alto - 10 * scaleY);
            canvas.showText("Sorteo: " + codSorteo);
            // Boleta un poco más abajo
            canvas.setTextMatrix(x + ancho - 45 * scaleX, y + alto - 16 * scaleY);
            canvas.showText("Boleta: " + (numeroBoleta != null ? numeroBoleta : "N/A"));
            canvas.endText();
            canvas.restoreState();
        }
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
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5F);
        canvas.setRGBColorStroke(0, 0, 0);
        /*
         * canvas.setRGBColorFill(0, 0, 0);
         * canvas.setFontAndSize(bf, 10.0F);
         * canvas.setTextMatrix(x + (ancho / 2) - 05.0F, y + alto - 32.0F);
         * canvas.showText(fecha);
         */
        // Texto Valor
        canvas.setLineWidth(0.7F);
        canvas.setFontAndSize(bf, 12.0F);
        // canvas.setTextMatrix(x + (ancho -45.0F) , y + alto - 20.0F);
        // canvas.showText("Valor");
        // valor de la boleta
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setLineWidth(0.7F);
        canvas.setFontAndSize(bf, 10.0F * scaleFont);
        canvas.setTextMatrix(x + 155 * scaleX, y + alto - 65.0F * scaleY);
        canvas.showText(valor);
        canvas.endText();
        canvas.restoreState();

        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5F);
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
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 8.0F * scaleFont);
        canvas.setTextMatrix(x + 10.0F * scaleX, y + alto - 35.0F * scaleY);
        canvas.showText(msg1);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 45.0F * scaleY);
        canvas.showText(msg2);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 55.0F * scaleY);
        canvas.showText(msg3);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 65.0F * scaleY);
        canvas.showText(msg4);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 75.0F * scaleY);
        canvas.showText(msg5);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 85.0F * scaleY);
        canvas.showText(msg6);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 95.0F * scaleY);
        canvas.showText(msg7);
        canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 105.0F * scaleY);
        canvas.showText(msg8);

        // canvas.setTextMatrix(x + 110.0F * scaleX, y + alto - 115.0F * scaleY);
        // canvas.showText(msg9);

        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 10.0F);

        // fecha
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
        return impresos;
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
