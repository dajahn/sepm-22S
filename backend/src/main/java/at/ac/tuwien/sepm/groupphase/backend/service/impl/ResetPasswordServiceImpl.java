package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ResetPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetRepository resetRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordServiceImpl(UserRepository userRepository, PasswordResetRepository resetRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resetRepository = resetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findUserByEmail(email);

        if (user == null) {
            throw new NotFoundException();
        }

        PasswordReset reset = new PasswordReset(user);
        resetRepository.save(reset);
        this.sendPasswordResetMail(reset);
    }

    @Override
    public void resetPasswordFromHash(UUID hash, String password) {
        PasswordReset reset = resetRepository.findByHash(hash);

        if (reset == null) {
            throw new NotFoundException();
        }

        User user = reset.getUser();
        passwordEncoder.encode(password);
        user.setPassword(password);
        userRepository.save(user);

        reset.setUsed(true);
        resetRepository.save(reset);
    }

    /**
     * Sends an email to the user containing the link to the password reset page including the hash.
     *
     * @param reset the password reset request
     */
    private void sendPasswordResetMail(PasswordReset reset) {
        // todo send mail
    }
}
