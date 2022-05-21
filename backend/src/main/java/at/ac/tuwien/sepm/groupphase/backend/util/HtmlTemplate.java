package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.exception.InternalServerException;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Template for creating the invoice pdf.
     */
    public static final HtmlTemplate PDF_INVOICE = new HtmlTemplate("pdf_invoice");

    /**
     * Template for creating the cancellation invoice pdf.
     */
    public static final HtmlTemplate PDF_CANCELLATION_INVOICE = new HtmlTemplate("pdf_cancellation_invoice");

    /**
     * Template for creating a pdf ticket.
     */
    public static final HtmlTemplate PDF_TICKET = new HtmlTemplate("pdf_ticket");

    /**
     * Template for generic emails, including a title and some content. <br>
     * parameters: title, content
     */
    public static final HtmlTemplate EMAIL_GENERIC = new HtmlTemplate("email_generic");  // todo: load images locally (not web)

    /**
     * Template for notifying the user about a new invoice.
     *
     * @see #EMAIL_GENERIC
     */
    public static final HtmlTemplate EMAIL_INVOICE_NOTIFICATION = EMAIL_GENERIC;

    /**
     * Template for notifying the user about their pdf tickets.
     *
     * @see #EMAIL_GENERIC
     */
    public static final HtmlTemplate EMAIL_TICKET_NOTIFICATION = EMAIL_GENERIC;

    /**
     * Template for generic emails, including a title and some content.
     */
    public static final HtmlTemplate EMAIL_PASSWORD_RESET_NOTIFICATION = new HtmlTemplate("email_password_reset"); // todo: load images locally (not web)


    /**
     * Stores global values for reusability and single point of truth.
     */
    private static final Map<String, String> GLOBALS = Stream.of(new String[][]{
        {"global.company.name", "Ticketline Inc."},
        {"global.company.title", "Ticketline"},
        {"global.company.address", "Some Street 10, 1010 Vienna - Austria"},
        {"global.company.uid", "ATU12345678"},
        {"global.company.homepage", "http://localhost:4200/#"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private final Template template;

    @Getter
    private final String path;

    /**
     * Setup and loading of the template.
     *
     * @param path the path of the template relative to the '/templates' folder of the package (without the .html file extension)
     */
    public HtmlTemplate(String path) {
        this.path = path;

        Template temp;
        try {
            temp = Mustache.compiler().compile(new FileReader("src/main/resources/templates/" + path + ".html"));
        } catch (FileNotFoundException e) {
            throw new InternalServerException();
        }
        this.template = temp;
    }

    /**
     * Replaces the values and returns the given string.
     *
     * @param values a map where key equals the key in the template and the value equals the value which should be replaced
     * @return the html template string after computing and replacing the values
     */
    public String compile(Map<String, Object> values) {
        LOGGER.trace("compile(Map<String, Object> values) with values={}", values);
        values.putAll(GLOBALS);
        return this.template.execute(values);
    }

    @Override
    public String toString() {
        return path;
    }
}
