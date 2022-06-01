package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.util.NewsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class NewsServiceImpl implements NewsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final NewsValidator newsValidator;

    private final FileService fileService;

    private NewsMapper newsMapper;
    private EventMapper eventMapper;

    public NewsServiceImpl(UserRepository userRepository, NewsRepository newsRepository,
                           NewsValidator newsValidator, NewsMapper newsMapper, FileService fileService, EventMapper eventMapper) {
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
        this.newsValidator = newsValidator;
        this.newsMapper = newsMapper;
        this.fileService = fileService;
        this.eventMapper = eventMapper;
    }

    @Override
    public News createNews(NewsDto newsDto) throws IOException {
        LOGGER.trace("createNews {}", newsDto);

        this.newsValidator.validateNews(newsDto);

        File file = this.fileService.create(newsDto.getFileDto());

        News news = News.builder()
                        .title(newsDto.getTitle())
                        .description(newsDto.getDescription())
                        .imageDescription(newsDto.getImageDescription())
                        .date(LocalDate.now())
                        .file(file).build();

        if (newsDto.getEventDto() != null) {
            LOGGER.debug("{}", newsDto.getEventDto());
            news.setEvent(this.eventMapper.eventDtoToEvent(newsDto.getEventDto()));
        }

        this.newsRepository.save(news);
        return news;
    }

    @Transactional
    @Override
    public PagedNewsDto getAll(int page, int size) {
        LOGGER.trace("getAll()");

        Long totalCount = newsRepository.count();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date", "id"));
        Page<News> news = newsRepository.findAll(pageable);

        List<NewsDto> newsDtos = mapOtherDtos(news.toList());

        PagedNewsDto pagedNewsDto = new PagedNewsDto();
        pagedNewsDto.setNews(newsDtos);
        pagedNewsDto.setTotalCount(totalCount);
        return pagedNewsDto;
    }

    @Transactional
    @Override
    public PagedNewsDto getUnread(String mail, int page, int size) {
        LOGGER.trace("getUnread()");
        User user = userRepository.findUserByEmail(mail);

        Pageable pageable = PageRequest.of(page, size);
        List<News> news = newsRepository.loadUnreadNews(user.getId(), pageable);
        List<NewsDto> newsDtos = mapOtherDtos(news);

        PagedNewsDto pagedNewsDto = new PagedNewsDto();
        pagedNewsDto.setNews(newsDtos);

        Long totalCount = newsRepository.getUnreadNewsCount(user.getId());
        pagedNewsDto.setTotalCount(totalCount);

        return pagedNewsDto;
    }

    @Override
    public NewsDto getById(Long id, String mail) {
        LOGGER.trace("getById({})", id);

        User user = userRepository.findUserByEmail(mail);

        Optional<News> n = this.newsRepository.findById(id);
        if (!n.isPresent()) {
            throw new NotFoundException("Could not find any news entry!");
        }
        Set<News> readNews = user.getReadNews();
        readNews.add(n.get());
        user.setReadNews(readNews);
        userRepository.save(user);

        News news = n.get();
        NewsDto newsDto = this.newsMapper.entityToNewsDto(news);

        FileDto fileDto = new FileDto();
        File f = news.getFile();
        Event e = news.getEvent();

        fileDto.setType(f.getType());
        fileDto.setUrl("/files/" + f.getId().toString());
        if (e != null) {
            newsDto.setEventDto(eventMapper.eventToEventDto(e));
        }

        newsDto.setFileDto(fileDto);

        return newsDto;
    }

    public List<NewsDto> mapOtherDtos(List<News> news) {
        List<NewsDto> newsDtos = news.stream().map(n -> this.newsMapper.entityToNewsDto(n)).toList();

        //Adds the corresponding fileDto and eventDto to the newsDto
        for (int i = 0; i < newsDtos.size(); i++) {
            FileDto fileDto = new FileDto();
            EventDto eventDto = new EventDto();

            File f = news.get(i).getFile();
            Event e = news.get(i).getEvent();

            if (e != null) {
                eventDto = eventMapper.eventToEventDto(e);
                newsDtos.get(i).setEventDto(eventDto);
            }

            fileDto.setType(f.getType());
            fileDto.setUrl("/files/" + f.getId().toString());

            newsDtos.get(i).setFileDto(fileDto);
        }

        return newsDtos;
    }
}
