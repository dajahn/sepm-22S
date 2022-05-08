package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;
import com.lowagie.text.DocumentException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

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
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handle exception
        }

        File fileEntity = new File();
        fileEntity.setType(MediaType.APPLICATION_PDF);
        fileEntity.setData(pdfBytes);

        try {
            // TODO remove after testing
            java.io.File outputFile = new java.io.File("temp/output.pdf");
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(pdfBytes);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileEntity;
    }
}
