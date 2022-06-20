package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ResetPasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ResetPasswordServiceTest {

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Rollback
    @Transactional
    public void givenUserForEmailExists_whenForgotPassword_thenPasswordResetIsCreated() {
        // GIVEN
        User user = userRepository.findById(1L).orElseThrow();
        String email = user.getEmail();

        // WHEN
        resetPasswordService.forgotPassword(email);

        // THEN
        PasswordReset reset = passwordResetRepository.findFirstByUser(user);

        // reset is not null / is stored in db
        assertNotNull(
            reset
        );
    }

    @Test
    @Rollback
    @Transactional
    public void givenUserForEmailDoesNotExists_whenForgotPassword_thenNotFoundExceptionIsThrown() {
        // GIVEN
        String email = "doesnot@exist.here";

        // THEN
        assertThrows(
            NotFoundException.class,
            () -> resetPasswordService.forgotPassword(email) // WHEN
        );
    }


    @Test
    @Rollback
    @Transactional
    public void givenPasswordResetExists_whenResetPassword_thenUsersPasswordIsUpdated() {
        // GIVEN
        User user = userRepository.findById(2L).orElseThrow();
        String email = user.getEmail();
        resetPasswordService.forgotPassword(email);
        PasswordReset reset = passwordResetRepository.findFirstByUser(user);

        String password = "new_password";

        // WHEN
        resetPasswordService.resetPasswordFromHash(reset.getHash(), password);

        // THEN
        user = userRepository.findById(2L).orElseThrow(); // update user entity (idk if needed)
        String userPassword = user.getPassword();

        // the password equals the new password
        assertTrue(
            passwordEncoder.matches(password, userPassword)
        );
    }
}
