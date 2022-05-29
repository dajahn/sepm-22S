package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/news")
public class NewsEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsService newsService;
    private final FileService fileService;
    private final NewsMapper newsMapper;

    public NewsEndpoint(NewsService newsService, FileService fileService, NewsMapper newsMapper) {
        this.newsService = newsService;
        this.fileService = fileService;
        this.newsMapper = newsMapper;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a new News Entry", security = @SecurityRequirement(name = "apiKey"))
    public NewsDto createNews(@RequestBody NewsDto newsDto) throws IOException {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);
        News news;

        try {
            news = this.newsService.createNews(newsDto);
        } catch (ValidationException e) {
            LOGGER.error("{}", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return this.newsMapper.entityToNewsDto(news);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Gets all the news Entries", security = @SecurityRequirement(name = "apiKey"))
    public PagedNewsDto getNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        LOGGER.info("GET /api/v1/news");
        return this.newsService.getAll(page, size);
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/{id}")
    @Operation(summary = "Gets news entry by id", security = @SecurityRequirement(name = "apiKey"))
    public NewsDto getNewsById(@PathVariable Long id) {
        LOGGER.info("Get /api/v1/news/{}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mail = authentication.getName();

        try {
            return this.newsService.getById(id, mail);
        } catch (NotFoundException e) {
            LOGGER.error("{}", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/unread")
    @Operation(summary = "Gets all unread news for user", security = @SecurityRequirement(name = "apiKey"))
    public PagedNewsDto getUnreadNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        LOGGER.info("Get /api/v1/news/unread");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mail = authentication.getName();

        return this.newsService.getUnread(mail, page, size);
    }
}
