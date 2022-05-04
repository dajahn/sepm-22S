package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;

import java.nio.file.Path;
import java.util.Map;

public interface PdfGenerationService {

    /**
     * Generate a pdf from a given html template using the supplied values and stores it at the given path.
     *
     * @param template   the path to the location of the templated used for generating the pdf
     * @param outputPath the path of the location where the output file should be stored
     * @param data       the data for replacing the values of the template
     */
    void generate(HtmlTemplate template, String outputPath, Map<String, String> data);
}
