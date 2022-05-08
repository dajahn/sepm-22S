package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;

import java.util.Map;

public interface EmailService {

    /**
     * Sends an email with the given template and the supplied parameters.
     *
     * @param template   the template used for creating the email
     * @param data       the data inserted in the template
     * @param subject    the subject of the email
     * @param recipient  the recipient of the email
     * @param attachment an optional attachment which is added to the email
     */
    void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment, String attachmentName);

    /**
     * Wrapper for {@link #send(HtmlTemplate, Map, String, String, File, String)}.
     *
     * @see #send(HtmlTemplate, Map, String, String, File, String)
     */
    default void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment) {
        this.send(template, data, subject, recipient, attachment, null);
    }

    /**
     * Wrapper for {@link #send(HtmlTemplate, Map, String, String, File, String)}.
     *
     * @see #send(HtmlTemplate, Map, String, String, File, String)
     */
    default void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient) {
        this.send(template, data, subject, recipient, null, null);
    }

    /**
     * Send a notification about a new invoice to the user including the invoice as an attachment.
     *
     * @param invoice    the invoice which the user should be notified about
     * @param attachment the invoice pfd file, which gets attached to the email
     */
    void sendInvoiceNotification(Invoice invoice, File attachment);
}
