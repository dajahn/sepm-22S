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
import org.springframework.web.bind.annotation.PostMapping;
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

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a new News Entry", security = @SecurityRequirement(name = "apiKey"))
    public NewsDto createNews(@ModelAttribute NewsDto newsDto) {
        LOGGER.info("POST /api/v1/news body: {}", newsDto);

        File file;
        News news;

        try {
            FileDto fileDto = new FileDto(null, MediaType.parseMediaType(newsDto.getImage().getContentType()), newsDto.getImage());

            file = this.fileService.create(fileDto);
            news = this.newsService.createNews(newsDto, file);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return this.newsMapper.entityToNewsDto(news);
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Gets all the news Entries", security = @SecurityRequirement(name = "apiKey"))
    public List<NewsDto> getNews() {

        List<News> news = this.newsService.getAll();

        List<NewsDto> newsDtos = news.stream().map(n -> this.newsMapper.entityToNewsDto(n)).toList();

        //TODO : return image in news element -> configure mapper?
        /*
        for (int i = 0; i < newsDtos.size(); i++) {
            File f = news.get(i).getFile();
            MultipartFile multipartFile = new MockMultipartFile("file", "file", f.getType().toString(), ImageUtility.decompressImage(f.getData()));
            newsDtos.get(i).setImage(multipartFile);
        }
        */
        return newsDtos;
    }
}
