package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedUserDto {
    List<UserDto> users;
    Long totalCount;
}
