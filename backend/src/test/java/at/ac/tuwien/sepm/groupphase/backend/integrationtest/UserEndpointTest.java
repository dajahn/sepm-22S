package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
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

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements UserTestData, AddressTestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private static ObjectMapper MAPPER;

    private CreateUpdateUserDto createUpdateUserDto;

    @BeforeAll
    public static void  initMapper() {
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();
    }

    @BeforeEach
    public void setCreateUserDto(){
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        createUpdateUserDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL)
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();
    }

    @Test
    public void givenNothing_whenCreate_thenCreatedUserWithAllSetPropertiesPlusId()
        throws Exception {
        createUpdateUserDto.setEmail("user"+  System.currentTimeMillis()+"@example.com");
        String body = MAPPER.writeValueAsString(createUpdateUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDto userDto = MAPPER.readValue(response.getContentAsString(),
            UserDto.class);
        assertNotNull(userDto.getId());
        assertAll(
            () -> assertEquals(createUpdateUserDto.getFirstName(), userDto.getFirstName()),
            () -> assertEquals(createUpdateUserDto.getLastName(), userDto.getLastName()),
            () -> assertEquals(createUpdateUserDto.getEmail(), userDto.getEmail()),
            () -> assertEquals(createUpdateUserDto.getAddress(), userDto.getAddress()),
            () -> assertEquals(createUpdateUserDto.getRole(), userDto.getRole())
        );
    }

    @Test
    public void givenNothing_whenCreateAdminUserWithAdminRights_thenCreatedUserWithAllSetPropertiesPlusId()
        throws Exception {
        createUpdateUserDto.setEmail("user"+  System.currentTimeMillis()+"@example.com");
        createUpdateUserDto.setRole(UserRole.ADMIN);
        String body = MAPPER.writeValueAsString(createUpdateUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDto userDto = MAPPER.readValue(response.getContentAsString(),
            UserDto.class);
        assertNotNull(userDto.getId());
        assertAll(
            () -> assertEquals(createUpdateUserDto.getFirstName(), userDto.getFirstName()),
            () -> assertEquals(createUpdateUserDto.getLastName(), userDto.getLastName()),
            () -> assertEquals(createUpdateUserDto.getEmail(), userDto.getEmail()),
            () -> assertEquals(createUpdateUserDto.getAddress(), userDto.getAddress()),
            () -> assertEquals(createUpdateUserDto.getRole(), userDto.getRole())
        );
    }

    @Test
    public void givenNothing_whenCreateAdminUserWithoutAdminRights_then422()
        throws Exception {
        createUpdateUserDto.setRole(UserRole.ADMIN);
        String body = MAPPER.writeValueAsString(createUpdateUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenCreateUserValueInvalid_then422()
        throws Exception {
        createUpdateUserDto.setFirstName(null);
        String body = MAPPER.writeValueAsString(createUpdateUserDto);

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPostInvalid_then400()
        throws Exception {
        String body = MAPPER.writeValueAsString("");

        MvcResult mvcResult = this.mockMvc.perform(post(USER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void givenNothing_whenUpdate_thenNoContentAndUserUpdatedInDb()
        throws Exception {
        createUpdateUserDto.setEmail("user"+ System.currentTimeMillis()+"@example.com");
        User inDb = userService.registerUser(createUpdateUserDto, true);
        String newMail = "new-email@example.com";
        createUpdateUserDto.setEmail(newMail);
        createUpdateUserDto.setFirstName("New Firstname");
        createUpdateUserDto.setLastName("New lastname");
        String body = MAPPER.writeValueAsString(createUpdateUserDto);

        final String uri = USER_BASE_URI+"/"+inDb.getId();
        MvcResult mvcResult = this.mockMvc.perform(put(uri)

                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        UserDto userDto = userMapper.userToUserDto(userService.findApplicationUserByEmail(newMail));
        assertAll(
            () -> assertEquals(createUpdateUserDto.getFirstName(), userDto.getFirstName()),
            () -> assertEquals(createUpdateUserDto.getLastName(), userDto.getLastName()),
            () -> assertEquals(createUpdateUserDto.getEmail(), userDto.getEmail()),
            () -> assertEquals(createUpdateUserDto.getAddress(), userDto.getAddress()),
            () -> assertEquals(createUpdateUserDto.getRole(), userDto.getRole())
        );
    }

    @Test
    public void givenNothing_whenUpdateUserValueInvalid_then422()
        throws Exception {
        User inDb = userService.registerUser(createUpdateUserDto, true);
        createUpdateUserDto.setEmail("asdfasdf");
        String body = MAPPER.writeValueAsString(createUpdateUserDto);
        final String uri = USER_BASE_URI+"/"+inDb.getId();

        MvcResult mvcResult = this.mockMvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenUpdateUserOnInvalidId_then404()
        throws Exception {
        String body = MAPPER.writeValueAsString(createUpdateUserDto);
        final String uri = USER_BASE_URI+"/"+"-999";

        MvcResult mvcResult = this.mockMvc.perform(put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenGetUsersOrderedByLockingState_then200() throws Exception {
        final String uri = USER_BASE_URI;
        UserSearchDto userSearchDto = new UserSearchDto();

        MvcResult mvcResult = this.mockMvc.perform(get(uri)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        ObjectMapper o = new ObjectMapper();
        StringReader reader = new StringReader(response.getContentAsString());

        Long usercount = userRepository.count();

        assertEquals(HttpStatus.OK.value(),response.getStatus());
    }


}
