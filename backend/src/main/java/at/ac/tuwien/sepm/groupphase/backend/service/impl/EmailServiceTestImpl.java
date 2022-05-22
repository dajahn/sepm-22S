package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Attachment;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

@Service
@Profile("test")
public class EmailServiceTestImpl extends EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, List<Attachment> attachments) {
        LOGGER.trace(
            "send(HtmlTemplate template, Map<String, Object> data, String subject, String recipient, List<Attachments> attachments) with template={} data={} subject={} recipient={} attachments={}",
            template.getPath(), data, subject, recipient, attachments);
    }
}
