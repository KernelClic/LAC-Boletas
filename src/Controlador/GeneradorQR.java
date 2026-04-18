package Controlador;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeneradorQR {
    public static String generarUUIDToken() {
        return UUID.randomUUID().toString();
    }

    public static com.itextpdf.text.Image crearCodigoQRBoleta(String tokenUUID, int anchoPixeles) throws Exception {
        String enlaceScaneo = "https://poco.absapex.net/apex/api_boletas/api_boletas/v1/qr/ver/" + tokenUUID;
        QRCodeWriter escritor = new QRCodeWriter();
        Map<EncodeHintType, ErrorCorrectionLevel> mapaSeguridad = new HashMap<>();
        mapaSeguridad.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        BitMatrix matrizDeBits = escritor.encode(enlaceScaneo, BarcodeFormat.QR_CODE, anchoPixeles, anchoPixeles,
                mapaSeguridad);
        BufferedImage imagenSwing = MatrixToImageWriter.toBufferedImage(matrizDeBits);
        return com.itextpdf.text.Image.getInstance(imagenSwing, null);
    }
}
