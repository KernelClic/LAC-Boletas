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
    public ArrayList<String> drawRectangle(PdfContentByte canvas, float x, float y, float ancho, float alto, float margen, float espacio, String titulo, String titulo1, String fecha, String valor, String msg1, String msg2, String msg3, String msg4, String msg5, int oportun, int colortexto, int marco, ArrayList<String> stmpPrint, int idx, Image img) throws DocumentException, IOException {
        ArrayList<String> impresos = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int length = 1000000;
        canvas.saveState();
        canvas.setGrayFill(0.9f);
        canvas.rectangle(x, y, ancho, alto);
        canvas.fillStroke();
        canvas.restoreState();
        canvas.saveState();
        img.setAbsolutePosition(x, y);
        img.scaleAbsoluteWidth(ancho);
        img.scaleAbsoluteHeight(alto);
        img.setBorder(15);
        img.setBorder(10);
        img.setBorderColor(BaseColor.BLACK);
        canvas.addImage(img);
        canvas.restoreState();
        for (int iopor = 0; iopor >= 0 && iopor < oportun; ++iopor) {
            sb.append(stmpPrint.get(idx + iopor) + "-");
        }
        String QrString = sb.toString();
        BarcodeQRCode my_code = new BarcodeQRCode(QrString, 1, 1, null);
        Image QRimage = my_code.getImage();
        canvas.saveState();
        QRimage.setAbsolutePosition(x + ancho - ancho / 2.0f - 32.0f, y + alto / 3.0f + 10.0f);
        QRimage.scaleAbsoluteWidth(63.0f);
        QRimage.scaleAbsoluteHeight(63.0f);
        QRimage.setBorderColor(BaseColor.BLACK);
        canvas.addImage(QRimage);
        canvas.restoreState();
        canvas.saveState();
        BaseFont bf = BaseFont.createFont();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(1.5f);
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
        canvas.setFontAndSize(bf, 20.0f);
        canvas.setTextMatrix(x + 5.0f, y + alto - 22.0f);
        canvas.showText(titulo);
        canvas.setTextMatrix(x + 5.0f, y + alto - 44.0f);
        canvas.showText(titulo1);
        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5f);
        canvas.setRGBColorStroke(0, 0, 0);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 18.0f);
        canvas.setTextMatrix(x + 5.0f, y + 27.0f);
        canvas.showText(fecha);
        canvas.setTextMatrix(x + 120.0f, y + 27.0f);
        canvas.showText(valor);
        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5f);
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
        canvas.setFontAndSize(bf, 11.0f);
        canvas.setTextMatrix(x + 5.0f, y + alto - 60.0f);
        canvas.showText(msg1);
        canvas.setTextMatrix(x + 5.0f, y + alto - 72.0f);
        canvas.showText(msg2);
        canvas.setTextMatrix(x + 5.0f, y + alto - 84.0f);
        canvas.showText(msg3);
        canvas.setTextMatrix(x + 5.0f, y + alto - 96.0f);
        canvas.showText(msg4);
        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.5f);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 10.5f);
        canvas.setTextMatrix(x + 3.0f, y + 7.0f);
        canvas.showText(msg5);
        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.setGrayFill(0.9f);
        if (marco == 1) {
            if (oportun == 1) {
                canvas.rectangle(x + 3.0f, y + alto - 115.0f, 55.0f, 20.0f);
            }
            if (oportun == 2) {
                canvas.rectangle(x + 3.0f, y + alto - 115.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 115.0f, 55.0f, 20.0f);
            }
            if (oportun == 3) {
                canvas.rectangle(x + 3.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 137.0f, 55.0f, 20.0f);
            }
            if (oportun == 4) {
                canvas.rectangle(x + 3.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 137.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 137.0f, 55.0f, 20.0f);
            }
            if (oportun == 5) {
                canvas.rectangle(x + 3.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 115.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 137.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 137.0f, 55.0f, 20.0f);
            }
            if (oportun == 6) {
                canvas.rectangle(x + 3.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 93.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 115.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 115.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 3.0f, y + alto - 137.0f, 55.0f, 20.0f);
                canvas.rectangle(x + 60.0f, y + alto - 137.0f, 55.0f, 20.0f);
            }
        }
        canvas.fillStroke();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.8f);
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 21.5f);
        if (oportun == 20) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 15));
            canvas.setTextMatrix(x + 6.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 16));
            canvas.setTextMatrix(x + 133.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 17));
            canvas.setTextMatrix(x + 6.0f, y + alto - 328.0f);
            canvas.showText(stmpPrint.get(idx + 18));
            canvas.setTextMatrix(x + 133.0f, y + alto - 328.0f);
            canvas.showText(stmpPrint.get(idx + 19));
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
            impresos.add(stmpPrint.get(idx + 15));
            impresos.add(stmpPrint.get(idx + 16));
            impresos.add(stmpPrint.get(idx + 17));
            impresos.add(stmpPrint.get(idx + 18));
            impresos.add(stmpPrint.get(idx + 19));
        }
        if (oportun == 19) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 15));
            canvas.setTextMatrix(x + 6.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 16));
            canvas.setTextMatrix(x + 133.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 17));
            canvas.setTextMatrix(x + 6.0f, y + alto - 328.0f);
            canvas.showText(stmpPrint.get(idx + 18));
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
            impresos.add(stmpPrint.get(idx + 15));
            impresos.add(stmpPrint.get(idx + 16));
            impresos.add(stmpPrint.get(idx + 17));
            impresos.add(stmpPrint.get(idx + 18));
        }
        if (oportun == 18) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 15));
            canvas.setTextMatrix(x + 6.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 16));
            canvas.setTextMatrix(x + 133.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 17));
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
            impresos.add(stmpPrint.get(idx + 15));
            impresos.add(stmpPrint.get(idx + 16));
            impresos.add(stmpPrint.get(idx + 17));
        }
        if (oportun == 17) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 15));
            canvas.setTextMatrix(x + 6.0f, y + alto - 306.0f);
            canvas.showText(stmpPrint.get(idx + 16));
            canvas.setTextMatrix(x + 133.0f, y + alto - 306.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
            impresos.add(stmpPrint.get(idx + 15));
            impresos.add(stmpPrint.get(idx + 16));
        }
        if (oportun == 16) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 15));
            canvas.setTextMatrix(x + 6.0f, y + alto - 306.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
            impresos.add(stmpPrint.get(idx + 15));
        }
        if (oportun == 15) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
            canvas.showText(stmpPrint.get(idx + 14));
            canvas.setTextMatrix(x + 133.0f, y + alto - 284.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
            impresos.add(stmpPrint.get(idx + 14));
        }
        if (oportun == 14) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 13));
            canvas.setTextMatrix(x + 6.0f, y + alto - 284.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
            impresos.add(stmpPrint.get(idx + 13));
        }
        if (oportun == 13) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
            canvas.showText(stmpPrint.get(idx + 12));
            canvas.setTextMatrix(x + 133.0f, y + alto - 262.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
            impresos.add(stmpPrint.get(idx + 12));
        }
        if (oportun == 12) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 11));
            canvas.setTextMatrix(x + 6.0f, y + alto - 262.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
            impresos.add(stmpPrint.get(idx + 11));
        }
        if (oportun == 11) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
            canvas.showText(stmpPrint.get(idx + 10));
            canvas.setTextMatrix(x + 133.0f, y + alto - 240.0f);
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
            impresos.add(stmpPrint.get(idx + 10));
        }
        if (oportun == 10) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 9));
            canvas.setTextMatrix(x + 6.0f, y + alto - 240.0f);
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
        if (oportun == 9) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            canvas.showText(stmpPrint.get(idx + 8));
            canvas.setTextMatrix(x + 133.0f, y + alto - 218.0f);
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
        if (oportun == 8) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 7));
            canvas.setTextMatrix(x + 6.0f, y + alto - 218.0f);
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
            impresos.add(stmpPrint.get(idx + 7));
        }
        if (oportun == 7) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            canvas.showText(stmpPrint.get(idx + 6));
            canvas.setTextMatrix(x + 133.0f, y + alto - 196.0f);
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
            impresos.add(stmpPrint.get(idx + 6));
        }
        if (oportun == 6) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            canvas.setTextMatrix(x + 133.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 5));
            canvas.setTextMatrix(x + 6.0f, y + alto - 196.0f);
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx + 5));
        }
        if (oportun == 5) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            canvas.setTextMatrix(x + 6.0f, y + alto - 174.0f);
            canvas.showText(stmpPrint.get(idx + 4));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx + 4));
        }
        if (oportun == 4) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            canvas.setTextMatrix(x + 133.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 3));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx + 3));
        }
        if (oportun == 3) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            canvas.setTextMatrix(x + 6.0f, y + alto - 152.0f);
            canvas.showText(stmpPrint.get(idx + 2));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx + 2));
        }
        if (oportun == 2) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            canvas.setTextMatrix(x + 133.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx + 1));
            impresos.add(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx + 1));
        }
        if (oportun == 1) {
            canvas.setTextMatrix(x + 6.0f, y + alto - 130.0f);
            canvas.showText(stmpPrint.get(idx));
            impresos.add(stmpPrint.get(idx));
        }
        canvas.endText();
        canvas.restoreState();
        return impresos;
    }

    public void nrosFaltantes(PdfContentByte canvas, ArrayList<String> nFatantes) throws DocumentException, IOException {
        canvas.saveState();
        BaseFont bf = BaseFont.createFont();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(1.5f);
        canvas.setRGBColorFill(0, 0, 0);
        canvas.setFontAndSize(bf, 16.0f);
        canvas.setTextMatrix(50.0f, 680.0f);
        canvas.showText("N\u00fameros Faltantes");
        canvas.endText();
        canvas.restoreState();
        canvas.saveState();
        canvas.beginText();
        canvas.setTextRenderingMode(2);
        canvas.setLineWidth(0.8f);
        canvas.setRGBColorStroke(255, 0, 0);
        canvas.setRGBColorFill(255, 0, 0);
        canvas.setFontAndSize(bf, 22.0f);
        Iterator<String> idx = nFatantes.iterator();
        int i = 1;
        int mnum = 10;
        int cad = 0;
        String prncad = "";
        canvas.setTextMatrix(10.0f, 640.0f);
        while (idx.hasNext()) {
            String elemento = idx.next();
            prncad = prncad + elemento + " ";
            if (i % mnum == 0) {
                switch (++cad) {
                    case 1: {
                        canvas.setTextMatrix(10.0f, 640.0f);
                        break;
                    }
                    case 2: {
                        canvas.setTextMatrix(10.0f, 620.0f);
                        break;
                    }
                    case 3: {
                        canvas.setTextMatrix(10.0f, 600.0f);
                        break;
                    }
                    case 4: {
                        canvas.setTextMatrix(10.0f, 580.0f);
                        break;
                    }
                    case 5: {
                        canvas.setTextMatrix(10.0f, 560.0f);
                        break;
                    }
                    case 6: {
                        canvas.setTextMatrix(10.0f, 540.0f);
                        break;
                    }
                    case 7: {
                        canvas.setTextMatrix(10.0f, 520.0f);
                        break;
                    }
                    case 8: {
                        canvas.setTextMatrix(10.0f, 500.0f);
                        break;
                    }
                    case 9: {
                        canvas.setTextMatrix(10.0f, 480.0f);
                        break;
                    }
                    case 10: {
                        canvas.setTextMatrix(10.0f, 460.0f);
                        break;
                    }
                    case 11: {
                        canvas.setTextMatrix(10.0f, 440.0f);
                    }
                }
                canvas.showText(prncad);
                prncad = "";
            }
            ++i;
        }
        if (i - 1 < 9) {
            canvas.showText(prncad);
        }
        canvas.endText();
        canvas.restoreState();
    }
}

