package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import com.sun.istack.ByteArrayDataSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment, String attachmentName) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(1025);
        mailSender.setUsername("");
        mailSender.setPassword("");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");

        String text = template.compile(data);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom("office@example.com");
            message.setRecipients(Message.RecipientType.TO, recipient);
            message.setSubject(subject);

            Multipart body = new MimeMultipart();
            MimeBodyPart messageBody = new MimeBodyPart();
            messageBody.setContent(text, "text/html");
            body.addBodyPart(messageBody);

            if (attachment != null) {
                MimeBodyPart attachmentBody = new MimeBodyPart();
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getType().toString());
                attachmentBody.setDataHandler(new DataHandler(dataSource));

                if (attachmentName != null) {
                    attachmentBody.setFileName(attachmentName);
                }
                body.addBodyPart(attachmentBody);
            }

            message.setContent(body);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendInvoiceNotification(Invoice invoice) {
        HashMap<String, Object> data = new HashMap<>();

        data.put("title", "New Invoice!");
        data.put("content", "You received a new invoice for your order at Ticketline. The invoice can be found in the attachment of this email.");

        this.send(
            HtmlTemplate.EMAIL_INVOICE_NOTIFICATION,
            data,
            "New Ticketline invoice",
            "invoice@example.com", // todo load recipient after merging order
            invoice.getPdf(),
            "invoice" + invoice.getIdentification().toString() + ".pdf"
        );
    }

    @Override
    public void sendCancellationInvoiceNotification(Invoice invoice) {
        HashMap<String, Object> data = new HashMap<>();

        data.put("title", "New Cancellation Invoice!");
        data.put("content", "You received a new cancellation invoice for your order at Ticketline. The cancellation invoice can be found in the attachment of this email.");

        this.send(
            HtmlTemplate.EMAIL_INVOICE_NOTIFICATION,
            data,
            "New Ticketline cancellation invoice",
            "invoice@example.com", // todo load recipient after merging order
            invoice.getPdf(),
            "invoice" + invoice.getIdentification().toString() + ".pdf"
        );
    }
}
