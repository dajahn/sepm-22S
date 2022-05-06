package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;

import java.io.File;

public interface EmailService {

    /**
     * Sends an email with the given template and the supplied parameters.
     *
     * @param template   the template used for creating the email
     * @param subject    the subject of the email
     * @param recipient  the recipient of the email
     * @param attachment an optional attachment which is added to the email
     */
    void send(HtmlTemplate template, String subject, String recipient, File attachment);

    /**
     * Wrapper for {@link #send(HtmlTemplate, String, String, File)}.
     *
     * @see #send(HtmlTemplate, String, String, File)
     */
    default void send(HtmlTemplate template, String subject, String recipient) {
        this.send(template, subject, recipient, null);
    }
}
