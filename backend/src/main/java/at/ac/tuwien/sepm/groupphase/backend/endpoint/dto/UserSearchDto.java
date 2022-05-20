package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import lombok.Data;

@Data
public class UserSearchDto {
    private UserStatus status;
    private UserRole role;
    private String nameSearch;
}
