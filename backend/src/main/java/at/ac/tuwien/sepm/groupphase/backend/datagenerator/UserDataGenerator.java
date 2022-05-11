package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int NUMBER_OF_USERS_TO_GENERATE = 1000;
    private static final int NUMBER_OF_ADMINS_TO_GENERATE = 10;
    private static final String TEST_USER_FIRST_NAME = "Firstname User #";
    private static final String TEST_USER_LAST_NAME = "Lastname User #";
    private static final String TEST_USER_EMAIL_PREFIX = "user";
    private static final String TEST_EMAIL_POSTFIX = "@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_ADDRESS_STREET = "Getreidemarkt #";
    private static final String TEST_ADDRESS_ZIP_CODE = "1010";
    private static final String TEST_ADDRESS_CITY = "Vienna";
    private static final Country TEST_ADDRESS_COUNTRY = Country.AT;

    private static final String TEST_ADMIN_FIRST_NAME = "Firstname Admin #";
    private static final String TEST_ADMIN_LAST_NAME = "Lastname Admin #";
    private static final String TEST_ADMIN_EMAIL_PREFIX = "admin";


    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateUserData() {
        if (userRepository.count() > 0) {
            LOGGER.debug("User already generated");
        } else {
            LOGGER.debug("Generating {} User entities and {} admin entities", NUMBER_OF_USERS_TO_GENERATE, NUMBER_OF_ADMINS_TO_GENERATE);
            Address address;
            User user;
            for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
                address = new Address();
                address.setStreet(TEST_ADDRESS_STREET + i);
                address.setZipCode(TEST_ADDRESS_ZIP_CODE);
                address.setCity(TEST_ADDRESS_CITY);
                address.setCountry(TEST_ADDRESS_COUNTRY);

                user = new User();
                user.setFirstName(TEST_USER_FIRST_NAME + i);
                user.setLastName(TEST_USER_LAST_NAME + i);
                user.setEmail(TEST_USER_EMAIL_PREFIX + i + TEST_EMAIL_POSTFIX);
                user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
                user.setAddress(address);
                user.setRole(UserRole.CUSTOMER);
                user.setStatus(UserStatus.OK);
                user.setLastNewsRead(LocalDateTime.now());
                userRepository.save(user);
            }
            for (int i = 0; i < NUMBER_OF_ADMINS_TO_GENERATE; i++) {
                address = new Address();
                address.setStreet(TEST_ADDRESS_STREET + (NUMBER_OF_USERS_TO_GENERATE + i));
                address.setZipCode(TEST_ADDRESS_ZIP_CODE);
                address.setCity(TEST_ADDRESS_CITY);
                address.setCountry(TEST_ADDRESS_COUNTRY);

                user = new User();
                user.setFirstName(TEST_ADMIN_FIRST_NAME + i);
                user.setLastName(TEST_ADMIN_LAST_NAME + i);
                user.setEmail(TEST_ADMIN_EMAIL_PREFIX + i + TEST_EMAIL_POSTFIX);
                user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
                user.setAddress(address);
                user.setRole(UserRole.ADMIN);
                user.setStatus(UserStatus.OK);
                user.setLastNewsRead(LocalDateTime.now());
                userRepository.save(user);
            }
        }
    }
}
