package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;

@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int NUMBER_OF_USERS_TO_GENERATE = 1000;
    private static final int NUMBER_OF_ADMINS_TO_GENERATE = 10;
    private static final String TEST_EMAIL_POSTFIX = "@example.com";
    private static final String TEST_PASSWORD = "password";


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
                Faker faker = new Faker();
                address = new Address();
                address.setStreet(faker.address().streetAddress());
                address.setZipCode(faker.address().zipCode());
                address.setCity(faker.address().city());
                address.setCountry(Country.valueOf(faker.address().countryCode()));
                // save their Pokémon as password
                user = User.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName())
                    .address(address).password(passwordEncoder.encode(faker.pokemon().name())).email(faker.name().firstName() + "." + faker.name().lastName() + TEST_EMAIL_POSTFIX)
                    .role(UserRole.CUSTOMER).status(UserStatus.OK).readNews(new HashSet<>()).build();
                userRepository.save(user);
            }
            for (int i = 0; i < NUMBER_OF_ADMINS_TO_GENERATE; i++) {
                Faker faker = new Faker();
                address = new Address();
                address.setStreet(faker.address().streetAddress());
                address.setZipCode(faker.address().zipCode());
                address.setCity(faker.address().city());
                address.setCountry(Country.valueOf(faker.address().countryCode()));
                // save their Pokémon as password
                user = User.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName())
                    .address(address).password(passwordEncoder.encode(faker.pokemon().name())).email(faker.name().firstName() + "." + faker.name().lastName() + TEST_EMAIL_POSTFIX)
                    .role(UserRole.ADMIN).status(UserStatus.OK).readNews(new HashSet<>()).build();
                userRepository.save(user);
            }

            //saving here one user with user1@example.com as email and one admin with admin1@example.com for testing reasons
            Faker faker = new Faker();
            address = new Address();
            address.setStreet(faker.address().streetAddress());
            address.setZipCode(faker.address().zipCode());
            address.setCity(faker.address().city());
            address.setCountry(Country.valueOf(faker.address().countryCode()));
            user = User.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .address(address).password(passwordEncoder.encode(TEST_PASSWORD)).email("admin" + TEST_EMAIL_POSTFIX)
                .role(UserRole.ADMIN).status(UserStatus.OK).readNews(new HashSet<>()).build();
            userRepository.save(user);
            user = User.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName())
                .address(address).password(passwordEncoder.encode(TEST_PASSWORD)).email("user1" + TEST_EMAIL_POSTFIX)
                .role(UserRole.ADMIN).status(UserStatus.OK).readNews(new HashSet<>()).build();
            userRepository.save(user);
        }
    }
}
