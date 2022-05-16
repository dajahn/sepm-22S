package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Profile("generateData")
@Component
public class NewsDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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

        //List<News> news = new ArrayList<>();
        FileInputStream fis = new FileInputStream("src/main/resources/newsMockImageBase64.txt");
        String fileString = IOUtils.toString(fis, Charset.availableCharsets().get("UTF-8"));
        byte[] toStore = Base64.getDecoder().decode(fileString);

        File file = File.builder()
            .type(MediaType.parseMediaType("image/jpeg"))
            .data(toStore)
            .build();

        this.fileRepository.save(file);

        News n = News.builder()
            .title("Hardstyle Tevvez Zyzz Concert")
            .description("A mixture of hardstyle and bodybuilding to honor lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor")
            .imageDescription("Premiering ob: 04/05/2022 18:30")
            .date(LocalDate.now())
            .file(file)
            .build();

        this.newsRepository.save(n);

        file = File.builder()
            .type(MediaType.parseMediaType("image/jpeg"))
            .data(toStore)
            .build();

        this.fileRepository.save(file);

        n = News.builder()
            .title("'Black Flame' broke a new Record!")
            .description("Their newest metal EP brought them into the lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor")
            .imageDescription("Premiering ob: 04/05/2022 18:30")
            .date(LocalDate.now())
            .file(file)
            .build();

        this.newsRepository.save(n);

        file = File.builder()
            .type(MediaType.parseMediaType("image/jpeg"))
            .data(toStore)
            .build();

        this.fileRepository.save(file);

        n = News.builder()
            .title("Premiere: Berserk in Concert")
            .description("After over 30 years the OST of the series Berserk lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor ")
            .imageDescription("Premiering ob: 04/05/2022 18:30")
            .date(LocalDate.now())
            .file(file)
            .build();

        this.newsRepository.save(n);

        file = File.builder()
            .type(MediaType.parseMediaType("image/jpeg"))
            .data(toStore)
            .build();

        this.fileRepository.save(file);

        n = News.builder()
            .title("Bitcoin Conference 2022")
            .description("Join and learn how Bitcoin will change the world lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor lorem ipsum dolor ")
            .imageDescription("Premiering ob: 04/05/2022 18:30")
            .date(LocalDate.now())
            .file(file)
            .build();

        this.newsRepository.save(n);
    }
}
