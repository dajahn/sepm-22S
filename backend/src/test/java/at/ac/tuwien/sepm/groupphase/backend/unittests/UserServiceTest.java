package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest implements UserTestData, AddressTestData {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void givenNothing_whenCreateUser_thenFindListWithOneElementAndFindUserById() {
        userRepository.deleteAll();
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        CreateUserDto userDto = CreateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL)
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();

        User x = userService.registerUser(userDto, false);
        User user = userMapper.createUserDtoToUser(userDto);
        user.setId(x.getId());
        user.setStatus(UserStatus.OK);
        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findById(x.getId())),
            () -> assertEquals(x, user)
        );
    }

    @Test
    public void givenIncorrectInput_whenCreateUser_thenThrowValidationException() {
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        CreateUserDto userDto = CreateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL)
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();

        ValidationException vex;
        //test firstname
        userDto.setFirstName(null);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("irstname"));
        userDto.setFirstName("");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("irstname"));
        userDto.setFirstName("   ");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("irstname"));
        userDto.setFirstName(USER_FIRST_NAME);

        //test lastname
        userDto.setLastName(null);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("astname"));
        userDto.setLastName("");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("astname"));
        userDto.setLastName("   ");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("astname"));
        userDto.setLastName(USER_LAST_NAME);

        //test password
        userDto.setPassword(null);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("assword"));
        userDto.setPassword("");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("assword"));
        userDto.setPassword("   ");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("assword"));
        userDto.setPassword("asdf12");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("assword"));
        userDto.setPassword(USER_PASSWORD);

        //test email
        userDto.setEmail(null);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("mail"));
        userDto.setEmail("");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("mail"));
        userDto.setEmail("   ");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("mail"));
        userDto.setEmail("asdjfhasjofdh");
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("mail"));
        userDto.setEmail(USER_EMAIL);


        //test role
        userDto.setRole(null);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ole"));
        userDto.setRole(UserRole.ADMIN);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ole"));
        userDto.setRole(UserRole.CUSTOMER);

        //test address - street
        a.setStreet(null);
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet("");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet("   ");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet(STREET);
        userDto.setAddress(a);

        //test address - zip code
        a.setZipCode(null);
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode("");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode("   ");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode(ZIP);
        userDto.setAddress(a);

        //test address - city
        a.setCity(null);
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity("");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity("   ");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity(CITY);
        userDto.setAddress(a);

        //test address - country
        a.setCountry(null);
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry("");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry("   ");
        userDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.registerUser(userDto, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry(String.valueOf(COUNTRY));
        userDto.setAddress(a);
    }
}
