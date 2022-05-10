package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;
import com.lowagie.text.DocumentException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private final FileRepository fileRepository;

    public PdfGenerationServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File generate(HtmlTemplate template, Map<String, Object> data) {
        String html = template.compile(data);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //step1: render html to memory
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        try {
            renderer.createPDF(out);
        } catch (DocumentException e) {
            e.printStackTrace();
            // TODO handle exception
        }
        byte[] pdfBytes = out.toByteArray();

        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle exception
        }

        File fileEntity = new File();
        fileEntity.setType(MediaType.APPLICATION_PDF);
        fileEntity.setData(pdfBytes);

        fileRepository.save(fileEntity); // todo remove after merging file management / replace with proper implementation

        return fileEntity;
    }
}
