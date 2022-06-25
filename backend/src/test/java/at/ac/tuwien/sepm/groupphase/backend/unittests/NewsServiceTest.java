package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData.ADMIN_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_BASE64_IMG;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_IMAGE_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_IMG_TYPE;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_TITLE;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class NewsServiceTest {
    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private FileRepository fileRepository;

    private News news;
    private File file;

    @BeforeEach
    public void beforeEach(){
        newsRepository.deleteAll();

        File file = File.builder()
            .type(TEST_NEWS_IMG_TYPE)
            .data(Base64.getDecoder().decode(TEST_NEWS_BASE64_IMG))
            .build();

        fileRepository.save(file);

        news = News.builder()
            .title(TEST_NEWS_TITLE)
            .description(TEST_NEWS_DESCRIPTION)
            .file(file)
            .imageDescription(TEST_NEWS_IMAGE_DESCRIPTION)
            .date(LocalDate.now())
            .build();
    }

    @Test
    @Transactional
    @Rollback
    public void givenNothing_whenFindByInvalidId_thenNotFoundException(){
        assertThrows(
            NotFoundException.class, () -> newsService.getById(100L,ADMIN_USER)
        );
    }

    @Test
    @Transactional
    public void givenNothing_whenCreateNewsEntry_thenGetCreatedNews() throws IOException {
        FileDto fileDto = new FileDto();
        fileDto.setType(TEST_NEWS_IMG_TYPE);
        fileDto.setImageBase64(TEST_NEWS_BASE64_IMG);

        NewsDto newsDto = newsMapper.entityToNewsDto(news);
        newsDto.setFileDto(fileDto);

        News n = newsService.createNews(newsDto);

        assertNotNull(n.getId());
        assertEquals(n.getTitle(),newsDto.getTitle());
        assertEquals(n.getDescription(),newsDto.getDescription());
    }

    @Test
    @Transactional
    public void givenNothing_whenGetAll_thenGetPagedNews() {
        //Check if right page size gets returned
        int size = 5;
        PagedNewsDto pagedNewsDto = newsService.getAll(0,5);

        Long newsCount = newsRepository.count();

        assertEquals(pagedNewsDto.getTotalCount(),newsCount);
        assertEquals(pagedNewsDto.getNews().size(),newsCount > size?size:newsCount);
    }

    @Test
    @Transactional
    public void givenNewNews_whenGetUnreadChangeIfRead_thenSetToRead() throws IOException {
        newsRepository.save(news);

        //Get first unread news for user
        PagedNewsDto pagedNewsDto = newsService.getUnread(ADMIN_EMAIL,0,5);NewsDto newsDto1 = pagedNewsDto.getNews().get(0);
        assertEquals(pagedNewsDto.getNews().size(),1);
        assertEquals(pagedNewsDto.getTotalCount(),1);
        //Read news
        newsService.getById(newsDto1.getId(),ADMIN_EMAIL);

        //Get first again should not be the same news
        PagedNewsDto second = newsService.getUnread(ADMIN_EMAIL,0,5);

        assertEquals(second.getNews().size(),0);
        assertEquals(second.getTotalCount(),0);
    }
}
