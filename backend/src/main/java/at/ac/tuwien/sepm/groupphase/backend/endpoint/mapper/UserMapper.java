package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User createUpdateUserDtoToUser(CreateUpdateUserDto userDto);

    UserDto userToUserDto(User user);

    CreateUpdateUserDto userToCreateUpdateUser(User user);


}
