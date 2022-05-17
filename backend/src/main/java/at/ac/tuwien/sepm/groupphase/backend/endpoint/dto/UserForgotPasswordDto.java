package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserForgotPasswordDto {
    @Email(message = "Email must be a valid email")
    @NotNull(message = "Email must not be null")
    public String email;
}