package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import com.sun.istack.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment, String attachmentName) {
        LOGGER.trace(
            "send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment, String attachmentName) with template={} data={} subject={} recipient={} attachment={} attachmentName={}",
            template.getPath(), data, subject, recipient, attachment, attachmentName);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(1025);
        mailSender.setUsername("");
        mailSender.setPassword("");

        try {
            mailSender.testConnection();

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");

            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom("office@example.com");
            message.setRecipients(Message.RecipientType.TO, recipient);
            message.setSubject(subject);

            Multipart body = new MimeMultipart();
            MimeBodyPart messageBody = new MimeBodyPart();
            String text = template.compile(data);
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
            throw new CouldNotDistributeException(e);
        }
    }

    @Override
    public void sendInvoiceNotification(Invoice invoice) {
        LOGGER.trace("sendInvoiceNotification(Invoice invoice) with invoice={}", invoice);

        HashMap<String, Object> data = new HashMap<>();

        data.put("title", "New Invoice!");
        data.put("content", "You received a new invoice for your order at Ticketline. The invoice can be found in the attachment of this email.");

        this.send(
            HtmlTemplate.EMAIL_INVOICE_NOTIFICATION,
            data,
            "New Ticketline invoice",
            invoice.getOrder().getUser().getEmail(),
            invoice.getPdf(),
            "invoice" + invoice.getIdentification().toString() + ".pdf"
        );
    }

    @Override
    public void sendCancellationInvoiceNotification(Invoice invoice) {
        LOGGER.trace("sendCancellationInvoiceNotification(Invoice invoice) with invoice={}", invoice);
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
