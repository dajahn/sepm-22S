package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.converter.MediaTypeConverter;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Base64;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.NEWS_BASE_URI;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_BASE64_IMG;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_IMG_TYPE;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NewsEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private NewsMapper newsMapper;

    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private News news;
    private File file;

    @BeforeEach
    public void beforeEach(){
        newsRepository.deleteAll();

        File file = File.builder()
            .type(TEST_NEWS_IMG_TYPE)
            .data(Base64.getDecoder().decode(TEST_NEWS_BASE64_IMG))
            .build();

        this.fileRepository.save(file);
        news = News.builder().title(TEST_NEWS_TITLE).description(TEST_NEWS_DESCRIPTION).file(file).date(LocalDate.now()).build();
    }

    @Test
    public void givenNothing_whenNewsEmpty_then422() throws Exception {
        String body = "";
        MvcResult mvcResult = this.mockMvc
            .perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        //TODO: should be unprocessable entity
        assertEquals(HttpStatus.BAD_REQUEST.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenNewsValid_then201() throws Exception {
        FileDto fileDto = new FileDto();
        fileDto.setType(TEST_NEWS_IMG_TYPE);
        fileDto.setImageBase64(TEST_NEWS_BASE64_IMG);

        NewsDto newsDto = newsMapper.entityToNewsDto(news);
        newsDto.setFileDto(fileDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
        .contentType(MediaType.APPLICATION_JSON).content(ow.writeValueAsString(newsDto))
        .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
        .andDo(print())
        .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenDescriptionTooLong_then422(){

    }

    @Test
    public void givenNothing_whenTitleTooLooLong_then422(){

    }

    @Test
    public void givenNothing_whenGetAll_then200(){

    }

}
