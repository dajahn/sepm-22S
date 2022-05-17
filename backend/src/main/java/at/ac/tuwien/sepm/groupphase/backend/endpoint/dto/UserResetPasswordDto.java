package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class UserResetPasswordDto {
    @NotNull(message = "Password must not be null")
    public String password;

    @NotNull(message = "Hash must not be null")
    public UUID hash;
}