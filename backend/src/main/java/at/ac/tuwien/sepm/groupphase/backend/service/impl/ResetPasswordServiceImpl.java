package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
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
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new NotFoundException();
        }

        PasswordReset reset = new PasswordReset(user);
        resetRepository.save(reset);
        System.out.println(reset.getHash()); // todo remove after development
        emailService.sendPasswordResetNotification(reset);
    }

    @Override
    public void resetPasswordFromHash(UUID hash, String password) {
        PasswordReset reset = resetRepository.findByHash(hash);

        if (reset == null) {
            throw new NotFoundException();
        }

        User user = reset.getUser();

        userValidator.validatePassword(password);

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        reset.setUsed(true);
        resetRepository.save(reset);
    }
}
