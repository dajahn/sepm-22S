package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.service.QRCodeGenerationService;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@SpringBootTest
@ActiveProfiles("test")
public class QRCodeGenerationServiceTest {

    @Autowired
    private QRCodeGenerationService qrCodeGenerationService;

    @Test
    @Transactional
    @Rollback
    public void givenUUIDInput_whenGenerateQRCode_thenQRCodeIsReturned() throws IOException, NotFoundException {
        // GIVEN
        String input = UUID.randomUUID().toString();

        // WHEN
        byte[] data = qrCodeGenerationService.generate(input);

        // THEN
        // not null
        assertNotNull(
            data
        );

        // data represents the input
        LuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(new ByteArrayInputStream(data)));
        String result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source))).getText();

        assertEquals(
            result,
            input
        );
    }
}
