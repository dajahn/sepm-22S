package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UserLockedException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepm.groupphase.backend.util.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserRepository userRepository;
    private final PasswordResetRepository resetRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserValidator userValidator;

    public ResetPasswordServiceImpl(UserRepository userRepository, PasswordResetRepository resetRepository, PasswordEncoder passwordEncoder, EmailService emailService, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.resetRepository = resetRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userValidator = userValidator;
    }

    @Override
    public void forgotPassword(String email) {
        LOGGER.trace("forgotPassword(String email) with email={}", email);
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new NotFoundException();
        }

        if (user.getRole() != UserRole.ADMIN && user.getStatus() == UserStatus.LOCKED) {
            throw new UserLockedException();
        }

        PasswordReset reset = new PasswordReset(user);
        resetRepository.save(reset);
        emailService.sendPasswordResetNotification(reset);
    }

    @Override
    public void resetPasswordFromHash(UUID hash, String password) {
        LOGGER.trace("resetPasswordFromHash(UUID hash, String password) with hash={} password={}", hash.toString(), password);
        PasswordReset reset = resetRepository.findByHash(hash);

        if (reset == null) {
            throw new NotFoundException();
        }

        User user = reset.getUser();

        if (user.getRole() != UserRole.ADMIN && user.getStatus() == UserStatus.LOCKED) {
            throw new UserLockedException();
        }

        userValidator.validatePassword(password);

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        reset.setUsed(true);
        resetRepository.save(reset);
    }
}
