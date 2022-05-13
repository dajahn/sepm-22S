package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class NewsValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void validateNews(NewsDto newsDto) {
        LOGGER.trace("validateNews() News:{}", newsDto);

        if (newsDto == null) {
            throw new ValidationException("News must not be null!");
        }
        if (newsDto.getTitle().trim().isEmpty()) {
            throw new ValidationException("News Title must not be empty!");
        }
        if (newsDto.getDescription().trim().isEmpty()) {
            throw new ValidationException("News Description most not be empty!");
        }
        if (newsDto.getTitle().length() > 255) {
            throw new ValidationException("New Title ist too long!");
        }
        if (newsDto.getDescription().length() > 1023) {
            throw new ValidationException("News Description ist too long!");
        }
    }
}
