package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;

import java.util.Map;

public interface PdfGenerationService {

    /**
     * Generate a pdf from a given html template using the supplied values and stores it at the given path.
     *
     * @param template the path to the location of the templated used for generating the pdf
     * @param data     the data for replacing the values of the template
     * @return FileEntity containing the pdfs data
     */
    File generate(HtmlTemplate template, Map<String, Object> data);
}
