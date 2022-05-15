package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements UserTestData, AddressTestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private static ObjectMapper MAPPER;

    private CreateUserDto createUserDto;

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
        createUserDto = CreateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL)
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();
    }

    @Test
    public void givenNothing_whenCreate_thenCreatedEventWithAllSetPropertiesPlusId()
        throws Exception {
        String body = MAPPER.writeValueAsString(createUserDto);

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
            () -> assertEquals(createUserDto.getFirstName(), userDto.getFirstName()),
            () -> assertEquals(createUserDto.getLastName(), userDto.getLastName()),
            () -> assertEquals(createUserDto.getEmail(), userDto.getEmail()),
            () -> assertEquals(createUserDto.getAddress(), userDto.getAddress()),
            () -> assertEquals(createUserDto.getRole(), userDto.getRole())
        );
    }

    @Test
    public void givenNothing_whenCreateUserValueInvalid_then422()
        throws Exception {
        createUserDto.setFirstName(null);
        String body = MAPPER.writeValueAsString(createUserDto);

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



}
