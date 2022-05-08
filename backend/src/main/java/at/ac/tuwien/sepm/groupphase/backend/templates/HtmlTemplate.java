package at.ac.tuwien.sepm.groupphase.backend.templates;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlTemplate {

    public static final HtmlTemplate PDF_INVOICE = new HtmlTemplate("pdf_invoice", new String[]{"test1", "test2"});

    private static final Map<String, String> GLOBALS = Stream.of(new String[][]{
        {"global.company.name", "Ticketline Inc."},
        {"global.company.address", "Some Street 10, 1010 Vienna - Austria"},
        {"global.company.uid", "ATU12345678"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private final String[] requiredKeys;
    private final Template template;

    /**
     * Setup and loading of the template.
     *
     * @param path         the path of the template relative to the '/templates' folder of the package (without the .html file extension)
     * @param requiredKeys a list of keys which are required in order to display the template properly
     */
    public HtmlTemplate(String path, String[] requiredKeys) {
        this.requiredKeys = requiredKeys;

        Template temp = null;
        try {
            temp = Mustache.compiler().compile(new FileReader("src/main/resources/templates/" + path + ".html"));
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // TODO: change to debug message
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
        for (String key : requiredKeys) {
            values.putIfAbsent(key, "");
            // TODO handle error properly
        }
        values.putAll(GLOBALS);
        return this.template.execute(values);
    }

}
