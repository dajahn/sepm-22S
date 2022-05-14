package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.converter.MediaTypeConverter;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import at.ac.tuwien.sepm.groupphase.backend.util.ImageUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
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

    //TODO: ROLE_ADMIN but admin user was not working
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a new News Entry", security = @SecurityRequirement(name = "apiKey"))
    public NewsDto createNews(@RequestBody NewsDto newsDto) throws IOException {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);
        News news;

        try {
            File file = this.fileService.create(newsDto.getFileDto());
            news = this.newsService.createNews(newsDto, file);
        } catch (ValidationException e) {
            LOGGER.error("{}", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return this.newsMapper.entityToNewsDto(news);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Gets all the news Entries", security = @SecurityRequirement(name = "apiKey"))
    public List<NewsDto> getNews() {
        LOGGER.info("GET /api/v1/news");

        List<NewsDto> newsDtos = this.newsService.getAll();

        return newsDtos;
    }

    @Secured("ROLE_USER")
    @GetMapping(path = "/{id}")
    @Operation(summary = "Gets news entry by id", security = @SecurityRequirement(name = "apiKey"))
    public NewsDto getNewsById(@PathVariable Long id) {
        LOGGER.info("Get /api/v1/news/{}", id);

        //TODO: handle not found
        return this.newsService.getById(id);
    }
}
