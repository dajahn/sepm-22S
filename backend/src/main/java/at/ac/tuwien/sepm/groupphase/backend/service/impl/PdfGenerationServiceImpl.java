package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    @Override
    public void generate(HtmlTemplate template, String location, Map<String, String> data) {
        throw new NotImplementedException();
    }
}
