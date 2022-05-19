package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.ToString;

import javax.validation.constraints.NotNull;

@ToString
public class UserResetPasswordDto {
    @NotNull(message = "Password must not be null")
    public String password;

    @NotNull(message = "Hash must not be null")
    public String hash;
}