package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@ToString
public class UserForgotPasswordDto {
    @Email(message = "Email must be a valid email")
    @NotNull(message = "Email must not be null")
    public String email;
}