package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.ImageUtility;
import com.github.javafaker.Faker;
import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Profile("generateData")
@Component
public class NewsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_NEWS_TO_GENERATE = 25;
    private final NewsRepository newsRepository;
    private final FileRepository fileRepository;

    public NewsDataGenerator(NewsRepository newsRepository, FileRepository fileRepository) {
        this.newsRepository = newsRepository;
        this.fileRepository = fileRepository;
    }

    @PostConstruct
    public void generateNews() throws FileNotFoundException {
        LOGGER.trace("generateNews()");

        if (newsRepository.count() > 0) {
            LOGGER.debug("News already generated!");
            return;
        }

        for (int i = 0; i < NUMBER_OF_NEWS_TO_GENERATE; i++) {
            Faker faker = new Faker();

            File file = File.builder()
                .type(MediaType.parseMediaType("image/jpeg"))
                .data(ImageUtility.randomJpeg(400, 300))
                .build();

            this.fileRepository.save(file);

            LocalDateTime t = LocalDateTime.of(LocalDate.ofInstant(faker.date().past(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0));

            News n = News.builder()
                .title("News: " + faker.artist().name() + " filler")
                .description(faker.lorem().fixedString(150))
                .imageDescription("Premiering on: " + t.getDayOfMonth() + "/" + t.getMonthValue() + "/" + t.getYear())
                .date(t.toLocalDate())
                .file(file)
                .build();

            this.newsRepository.save(n);
        }
    }
}
