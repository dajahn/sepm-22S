package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
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
import org.springframework.transaction.annotation.Transactional;

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
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        CreateUpdateUserDto userDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();

        User x = userService.registerUser(userDto, false);
        User user = userMapper.createUpdateUserDtoToUser(userDto);
        user.setId(x.getId());
        user.setStatus(UserStatus.OK);
        assertAll(
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
        CreateUpdateUserDto userDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
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
        userDto.setEmail("user"+System.currentTimeMillis()+"@example.com");


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

    @Test
    public void givenNothing_whenUpdateUserWithUserInDB_thenFindUpdatedFindUserById() {
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        CreateUpdateUserDto userDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();

        User user = userService.registerUser(userDto, false);

        CreateUpdateUserDto updateUserDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).status(UserStatus.OK).address(a).build();

        userService.updateUser(updateUserDto, user.getId(), false);
        User updated = userRepository.findById(user.getId()).get();
        updateUserDto.setPassword(updated.getPassword());
        CreateUpdateUserDto updatedDto = userMapper.userToCreateUpdateUser(updated);
        assertEquals(updatedDto, updateUserDto);
    }

    @Test
    public void givenIncorrectInput_whenUpdateUser_thenThrowValidationException() {
        AddressDto a = new AddressDto();
        a.setCity(CITY);
        a.setCountry(String.valueOf(COUNTRY));
        a.setStreet(STREET);
        a.setZipCode(ZIP);
        CreateUpdateUserDto insertUserDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).address(a).build();
        User user = userService.registerUser(insertUserDto, false);

        CreateUpdateUserDto updateUserDto = CreateUpdateUserDto.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email("user"+System.currentTimeMillis()+"@example.com")
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).status(UserStatus.OK).address(a).build();

        ValidationException vex;
        //test ID
        Long tmpUserId = null;
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, tmpUserId, false));
        assertTrue(vex.getMessage().contains("ID"));
        Long finalUserId = user.getId();


        //test firstname
        updateUserDto.setFirstName(null);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("irstname"));
        updateUserDto.setFirstName("");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("irstname"));
        updateUserDto.setFirstName("   ");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("irstname"));
        updateUserDto.setFirstName(USER_FIRST_NAME);

        //test lastname
        updateUserDto.setLastName(null);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("astname"));
        updateUserDto.setLastName("");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("astname"));
        updateUserDto.setLastName("   ");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("astname"));
        updateUserDto.setLastName(USER_LAST_NAME);

        //test password
        updateUserDto.setPassword(null);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("assword"));
        updateUserDto.setPassword("");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("assword"));
        updateUserDto.setPassword("   ");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("assword"));
        updateUserDto.setPassword("asdf12");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("assword"));
        updateUserDto.setPassword(USER_PASSWORD);

        //test email
        updateUserDto.setEmail(null);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("mail"));
        updateUserDto.setEmail("");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("mail"));
        updateUserDto.setEmail("   ");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("mail"));
        updateUserDto.setEmail("asdjfhasjofdh");
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("mail"));
        updateUserDto.setEmail("user"+System.currentTimeMillis()+"@example.com");


        //test role
        updateUserDto.setRole(null);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ole"));
        updateUserDto.setRole(UserRole.ADMIN);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ole"));
        updateUserDto.setRole(UserRole.CUSTOMER);

        //test address - street
        a.setStreet(null);
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet("");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet("   ");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("treet"));
        a.setStreet(STREET);
        updateUserDto.setAddress(a);

        //test address - zip code
        a.setZipCode(null);
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode("");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode("   ");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ip"));
        a.setZipCode(ZIP);
        updateUserDto.setAddress(a);

        //test address - city
        a.setCity(null);
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity("");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity("   ");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto,finalUserId, false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ity"));
        a.setCity(CITY);
        updateUserDto.setAddress(a);

        //test address - country
        a.setCountry(null);
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry("");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry("   ");
        updateUserDto.setAddress(a);
        vex = assertThrows(ValidationException.class, () -> userService.updateUser(updateUserDto, finalUserId,false));
        assertTrue(vex.getMessage().contains("ddress"));
        assertTrue(vex.getMessage().contains("ountry"));
        a.setCountry(String.valueOf(COUNTRY));
        updateUserDto.setAddress(a);
    }

    @Test
    @Transactional
    public void givenNothing_addFailedLoginAttemptToUser_thenIncreaseAttempts(){
        User u = userRepository.findAll().get(0);
        int failedBefore = u.getFailedLoginAttempts();

        UserLoginDto userLoginDto = new UserLoginDto();

        userService.addFailedLoginAttemptToUser(userMapper.userToUserLoginDto(u));
        int failedAfter = u.getFailedLoginAttempts();

        assertEquals(failedBefore+1,failedAfter);
    }

}
