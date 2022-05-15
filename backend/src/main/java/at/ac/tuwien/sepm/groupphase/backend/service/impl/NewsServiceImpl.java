package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.util.ImageUtility;
import at.ac.tuwien.sepm.groupphase.backend.util.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;

    private final FileService fileService;

    private NewsMapper newsMapper;

    public NewsServiceImpl(NewsRepository newsRepository, NewsValidator newsValidator, NewsMapper newsMapper, FileService fileService) {
        this.newsRepository = newsRepository;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
        this.fileService = fileService;
    }

    @Override
    public News createNews(NewsDto newsDto) throws IOException {
        LOGGER.trace("createNews {}", newsDto);

        this.newsValidator.validateNews(newsDto);

        File file = this.fileService.create(newsDto.getFileDto());
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
    public List<NewsDto> getAll() {
        LOGGER.trace("getAll()");

        List<News> news = this.newsRepository.findAll();
        List<NewsDto> newsDtos = news.stream().map(n -> this.newsMapper.entityToNewsDto(n)).toList();

        //Adds the corresponding filedto to the newsdto
        for (int i = 0; i < newsDtos.size(); i++) {
            FileDto fileDto = new FileDto();
            File f = news.get(i).getFile();

            fileDto.setType(f.getType());
            fileDto.setUrl("/files/" + f.getId().toString());

            newsDtos.get(i).setFileDto(fileDto);
        }

        return newsDtos;
    }

    @Override
    public NewsDto getById(Long id) {
        LOGGER.trace("getById({})", id);

        Optional<News> n = this.newsRepository.findById(id);
        if (!n.isPresent()) {
            throw new NotFoundException();
        }

        News news = n.get();
        NewsDto newsDto = this.newsMapper.entityToNewsDto(news);

        FileDto fileDto = new FileDto();
        File f = news.getFile();

        fileDto.setType(f.getType());
        fileDto.setUrl("/files/" + f.getId().toString());

        newsDto.setFileDto(fileDto);

        return newsDto;
    }
}
