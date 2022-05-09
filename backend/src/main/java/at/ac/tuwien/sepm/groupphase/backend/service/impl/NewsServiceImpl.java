package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.util.ImageUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NewsRepository newsRepository;

    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public News createNews(NewsDto newsDto, File file) throws IOException {
        LOGGER.debug("createNews {}", newsDto);
        LOGGER.info(newsDto.getImage().getOriginalFilename());
        LOGGER.info(String.valueOf(newsDto.getImage().getSize()));

        //TODO: create reference with event
        News news = News.builder()
            .title(newsDto.getTitle())
            .description(newsDto.getDescription())
            .date(newsDto.getDate())
            .file(file).build();

        this.newsRepository.save(news);
        return news;
    }

    @Override
    public List<News> getAll() {
        return this.newsRepository.findAll();
    }
}
