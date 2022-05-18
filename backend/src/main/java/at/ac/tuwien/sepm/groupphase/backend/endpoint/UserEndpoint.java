package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotLockUserException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    public UserEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Gets all users by UserSearchFilter", security = @SecurityRequirement(name = "apiKey"))
    public List<UserDto> getUserOrderByLockedState(UserSearchDto userSearchDto) {
        LOGGER.info("getUserOrderByLockedState() with: {}", userSearchDto);
        return userMapper.entitiesToUserDto(userService.getUser(userSearchDto));
    }

    @GetMapping("/unlock/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Unlocks User by id", security = @SecurityRequirement(name = "apiKey"))
    public UserDto unlockUserById(@PathVariable Long id) {
        LOGGER.info("unlockUserById({})", id);
        return userMapper.userToUserDto(userService.unlockUserById(id));
    }

    @GetMapping("/lock/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Locks User by id", security = @SecurityRequirement(name = "apiKey"))
    public UserDto lockUserById(@PathVariable Long id) {
        LOGGER.info("lockUserById({})", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mail = authentication.getName();
        try {
            return userMapper.userToUserDto(userService.lockUserById(id, mail));
        } catch (CouldNotLockUserException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PermitAll
    @Operation(summary = "Creates a new Customer", security = @SecurityRequirement(name = "apiKey"))
    public UserDto createUser(@RequestBody CreateUserDto userDto)  {
        LOGGER.info("POST /api/v1/users body: {}", userDto);
        try {
            return userMapper.userToUserDto(userService.registerUser(userDto, false));
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }
}
