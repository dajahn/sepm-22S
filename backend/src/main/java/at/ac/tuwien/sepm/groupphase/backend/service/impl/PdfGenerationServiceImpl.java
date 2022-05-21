package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotGeneratePdfException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import com.lowagie.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FileRepository fileRepository;

    public PdfGenerationServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File generate(HtmlTemplate template, Map<String, Object> data) {
        LOGGER.trace("generate(HtmlTemplate template, Map<String, Object> data) with template={} data={}", template.getPath(), data);
        String html = template.compile(data);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        try {
            renderer.createPDF(out);
        } catch (DocumentException e) {
            throw new CouldNotGeneratePdfException(e);
        }
        byte[] pdfBytes = out.toByteArray();

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new CouldNotGeneratePdfException(e);
        }

        File fileEntity = new File();
        fileEntity.setType(MediaType.APPLICATION_PDF);
        fileEntity.setData(pdfBytes);

        fileRepository.save(fileEntity);

        return fileEntity;
    }
}
