package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements UserTestData, AddressTestData {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    @Transactional
    public void givenNothing_whenSaveUser_thenFindListWithOneElementAndFindUserById() {
        userRepository.deleteAll();
        Address a = new Address(STREET, ZIP, CITY, COUNTRY);
        User u = User.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL)
            .password(USER_PASSWORD).role(UserRole.CUSTOMER).status(UserStatus.OK).address(a).build();
        User x = userRepository.save(u);
        u.setId(x.getId());
        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findById(x.getId())),
            () -> assertEquals(x, u)
        );
    }
}
