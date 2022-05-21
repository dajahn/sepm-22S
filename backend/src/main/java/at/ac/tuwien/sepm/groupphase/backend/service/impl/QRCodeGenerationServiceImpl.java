package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.QRCodeGenerationService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Service
public class QRCodeGenerationServiceImpl implements QRCodeGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public byte[] generate(String input) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // configure code generation
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, 0); // remove whitespace

        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(input, BarcodeFormat.QR_CODE, 128, 128, hintMap);
        } catch (WriterException e) {
            e.printStackTrace(); // todo handle exception
            return null;
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig con = new MatrixToImageConfig(0xFF000000, 0x00FFFFFF);

        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
        } catch (IOException e) {
            e.printStackTrace(); // todo handle exception
        }

        return pngOutputStream.toByteArray();
    }
}

