package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Attachment;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface EmailService {

    /**
     * Sends an email with the given template and the supplied parameters.
     *
     * @param template    the template used for creating the email
     * @param data        the data inserted in the template
     * @param subject     the subject of the email
     * @param recipient   the recipient of the email
     * @param attachments a list of attachments
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there is an error while sending the email
     */
    void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, List<Attachment> attachments);

    /**
     * Wrapper for {@link #send(HtmlTemplate, Map, String, String, List)}.
     *
     * @see #send(HtmlTemplate, Map, String, String, List)
     */
    default void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, Attachment attachment) {
        this.send(template, data, subject, recipient, new ArrayList<>(List.of(attachment)));
    }

    /**
     * Wrapper for {@link #send(HtmlTemplate, Map, String, String, List)}.
     *
     * @see #send(HtmlTemplate, Map, String, String, List)
     */
    default void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, File attachment) {
        this.send(template, data, subject, recipient, new Attachment(attachment, null));
    }

    /**
     * Wrapper for {@link #send(HtmlTemplate, Map, String, String, List)}.
     *
     * @see #send(HtmlTemplate, Map, String, String, List)
     */
    default void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient) {
        this.send(template, data, subject, recipient, (List<Attachment>) null);
    }

    /**
     * Send a notification about a new invoice to the user including the invoice as an attachment.
     *
     * @param invoice the invoice which the user should be notified about
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there is an error while sending the email
     */
    void sendInvoiceNotification(Invoice invoice);

    /**
     * Send a notification about a canceled invoice to the user including the cancellation invoice as an attachment.
     *
     * @param invoice the cancellation invoice which the user should be notified about
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there is an error while sending the email
     */
    void sendCancellationInvoiceNotification(Invoice invoice);

    /**
     * Send a notification about a new password reset request including the hash for resetting the password.
     *
     * @param reset the password reset request item
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there is an error while sending the email
     */
    void sendPasswordResetNotification(PasswordReset reset);

    /**
     * Send a notification about pdf ticket to the user.
     *
     * @param order the order which the user should be notified about.
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there is an error while sending the email
     */
    void sendTicketNotification(TicketOrder order);
}
