package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.util.Formatter;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class PdfGenerationServiceTest implements TestData {

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private EmailService emailService;

    @Test
    public void givenInvoiceExists_whenNewPdfIsGenerated_thenPdfIsGenerated() {

        Map<String, Object> data = new HashMap<>();
        data.put("invoice.number", "2022-124");
        data.put("tickets", Arrays.asList(new Object() {
            final String sector = "Standing";
            final int amount = 5;
            final String singlePrice = Formatter.formatPrice(5.5f);
            final String totalPrice = Formatter.formatPrice(5 * 5.5f);
        }, new Object() {
            final String sector = "VIP";
            final int amount = 2;
            final String singlePrice = Formatter.formatPrice(13.2f);
            final String totalPrice = Formatter.formatPrice(2 * 13.2f);
        }));
        data.put("totalPrice", Formatter.formatPrice(22.5f));
        data.put("totalTaxes", Formatter.formatPrice(2.5f));

        data.put("event.title", "Sport records tour");
        data.put("event.date", "8.5.2022 18:00 - 20:00");
        data.put("event.location", "WUK Vienna");

        File pdf = pdfGenerationService.generate(HtmlTemplate.PDF_INVOICE, data);

        System.out.println(pdf);

        emailService.sendInvoiceNotification(new Invoice(InvoiceType.NORMAL));

    }
}
