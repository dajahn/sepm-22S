package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.templates.HtmlTemplate;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements at.ac.tuwien.sepm.groupphase.backend.service.EmailService {

    @Override
    public void send(HtmlTemplate template, String subject, String recipient, File attachment) {
        throw new NotImplementedException();
    }
}
