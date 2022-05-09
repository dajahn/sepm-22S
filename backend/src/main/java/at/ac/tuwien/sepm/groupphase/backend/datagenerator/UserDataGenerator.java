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

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateMessage() {
        if (userRepository.findAll().size() > 0) {
            LOGGER.debug("User already generated");
        } else {
            LOGGER.debug("Generating 2 User entities");
            Address address = new Address();
            address.setStreet("Getreidemarkt 7");
            address.setZipCode("1010");
            address.setCity("Vienna");
            address.setCountry(Country.AT);

            User user = new User();
            user.setFirstName("Hans");
            user.setLastName("Gittenberger");
            user.setEmail("user@email.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setAddress(address);
            user.setRole(UserRole.CUSTOMER);
            user.setStatus(UserStatus.OK);
            user.setLastNewsRead(LocalDateTime.now());
            User admin = new User();
            admin.setFirstName("Administrator");
            admin.setLastName("der Terminator");
            admin.setEmail("admin@email.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setAddress(address);
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(UserStatus.OK);
            admin.setLastNewsRead(LocalDateTime.now());


            userRepository.save(user);
            userRepository.save(admin);
        }

    }
}
