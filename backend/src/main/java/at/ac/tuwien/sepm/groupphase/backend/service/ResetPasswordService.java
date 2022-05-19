package at.ac.tuwien.sepm.groupphase.backend.service;

import java.util.UUID;

public interface ResetPasswordService {
    /**
     * Trigger the reset-password workflow for a user by a supplied email address.
     *
     * @param email the email of the user which password should be reset
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if there is no user with the supplied email
     */
    void forgotPassword(String email);

    /**
     * Trigger the reset-password workflow for a user by a supplied email address.
     *
     * @param hash     the hash referencing the reset request
     * @param password the new password
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException   if there is no matching / valid entry stored
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException if the password does not meet the required criteria
     */
    void resetPasswordFromHash(UUID hash, String password);
}
