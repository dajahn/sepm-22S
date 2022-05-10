package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.util.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;

    public NewsServiceImpl(NewsRepository newsRepository, NewsValidator newsValidator) {
        this.newsRepository = newsRepository;
        this.newsValidator = newsValidator;
    }

    @Override
    public News createNews(NewsDto newsDto, File file) throws IOException {
        LOGGER.debug("createNews {}", newsDto);
        this.newsValidator.validateNews(newsDto);

        //TODO: create reference with event
        News news = News.builder()
            .title(newsDto.getTitle())
            .description(newsDto.getDescription())
            .date(LocalDate.now())
            .file(file).build();

        this.newsRepository.save(news);
        return news;
    }

    @Override
    public List<News> getAll() {
        List<News> news = this.newsRepository.findAll();
        return news;
    }
}
