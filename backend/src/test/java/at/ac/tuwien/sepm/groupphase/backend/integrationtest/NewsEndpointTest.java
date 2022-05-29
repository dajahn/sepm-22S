package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedNewsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.converter.MediaTypeConverter;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoDeserializer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.StringUtil;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.NEWS_BASE_URI;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_BASE64_IMG;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_IMAGE_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_IMG_TYPE;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_NEWS_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private static ObjectMapper MAPPER;


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

    @BeforeAll
    public static void  initMapper() {
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();
        SimpleModule serializer =
            new SimpleModule("FileDtoSerializer", new Version(1, 0, 0, null, null, null));
        serializer.addSerializer(FileDto.class, new FileDtoSerializer());

        SimpleModule deserializer =
            new SimpleModule("FileDtoDeserializer", new Version(1, 0, 0, null, null, null));
        deserializer.addDeserializer(FileDto.class, new FileDtoDeserializer());

        MAPPER.registerModule(deserializer);
        MAPPER.registerModule(serializer);
    }

    @Test
    public void givenNothing_whenNewsEmpty_then422() throws Exception {
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle("");
        String body = MAPPER.writeValueAsString(newsDto);

        MvcResult mvcResult = this.mockMvc
            .perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenNewsValid_then201() throws Exception {
        FileDto fileDto = new FileDto();
        fileDto.setType(TEST_NEWS_IMG_TYPE);
        fileDto.setImageBase64(TEST_NEWS_BASE64_IMG);

        NewsDto newsDto = newsMapper.entityToNewsDto(news);
        newsDto.setFileDto(fileDto);

        String body = MAPPER.writeValueAsString(newsDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
        .contentType(MediaType.APPLICATION_JSON).content(body)
        .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
        .andDo(print())
        .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        NewsDto newsDto1 = MAPPER.readValue(response.getContentAsString(),
            NewsDto.class);

        assertNotNull(newsDto1.getId());
        assertEquals(HttpStatus.CREATED.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenDescriptionTooLong_then422() throws Exception {
        NewsDto newsDto = new NewsDto();
        newsDto.setDescription(StringUtils.repeat("a",65536));
        String body = MAPPER.writeValueAsString(newsDto);

        MvcResult mvcResult = this.mockMvc
            .perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenTitleTooLooLong_then422() throws Exception{
        NewsDto newsDto = new NewsDto();
        newsDto.setTitle(StringUtils.repeat("t",256));
        String body = MAPPER.writeValueAsString(newsDto);

        MvcResult mvcResult = this.mockMvc
            .perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(),response.getStatus());
    }

    @Test
    public void givenNothing_whenGetAll_then200() throws Exception {

        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc
            .perform(get(NEWS_BASE_URI).header(securityProperties.getAuthHeader(),jwtTokenizer.getAuthToken(ADMIN_USER,ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        PagedNewsDto pagedNewsDto = MAPPER.readValue(response.getContentAsString(),PagedNewsDto.class);
        assertEquals(1,pagedNewsDto.getNews().size());
    }

}
