package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmailSeviceTest implements TestData {

    @Autowired
    private EmailService emailService;

    @Test
    public void givenInvoiceExists_whenNewPdfIsGenerated_thenPdfIsGenerated() { // TODO change name
        HashMap<String, Object> data = new HashMap<>();
        
        data.put("title", "New Invoice!");
        data.put("content", "You received a new invoice for your order at Ticketline. The invoice can be found in the attachment of this email.");

        emailService.send(HtmlTemplate.EMAIL_INVOICE_NOTIFICATION, data, "You new invoice", "invoice@example.com");
    }
}
