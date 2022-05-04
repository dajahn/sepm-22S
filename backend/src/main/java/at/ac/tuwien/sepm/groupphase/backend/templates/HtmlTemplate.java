package at.ac.tuwien.sepm.groupphase.backend.templates;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HtmlTemplate {

    public static HtmlTemplate EXAMPLE = new HtmlTemplate("example.html", new String[]{"test", "test2"});

    private File file;
    private final String[] requiredKeys;

    /**
     * Setup and loading of the template.
     *
     * @param path         the path of the template relative to the '/templates' folder of the package
     * @param requiredKeys a string array of keys which are required in order to display the template properly
     */
    public HtmlTemplate(String path, String[] requiredKeys) {
        this.requiredKeys = requiredKeys;
        // TODO: define logic for loading template;
    }

    /**
     * Replaces the values and returns the given string.
     *
     * @param values a map where key equals the key in the template and the value equals the value which should be replaced
     * @return the html template string after computing and replacing the values
     */
    public String compile(Map<String, String> values) {
        return null;
    }

}
