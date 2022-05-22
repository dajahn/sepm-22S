package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserForgotPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotLockUserException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.ResetPasswordService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final ResetPasswordService resetPasswordService;
    private final UserMapper userMapper;

    public UserEndpoint(UserService userService, ResetPasswordService resetPasswordService, UserMapper userMapper) {
        this.userService = userService;
        this.resetPasswordService = resetPasswordService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Gets all users by UserSearchFilter", security = @SecurityRequirement(name = "apiKey"))
    public List<UserDto> getUserOrderByLockedState(UserSearchDto userSearchDto) {
        LOGGER.info("getUserOrderByLockedState() with: {}", userSearchDto);
        return userMapper.entitiesToUserDto(userService.getUser(userSearchDto));
    }

    @PutMapping("/unlock/{id}")
    @Secured("ROLE_ADMIN")
    @Operation(summary = "Unlocks User by id", security = @SecurityRequirement(name = "apiKey"))
    public UserDto unlockUserById(@PathVariable Long id) {
        LOGGER.info("unlockUserById({})", id);
        try {
            return userMapper.userToUserDto(userService.unlockUserById(id));
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/lock/{id}")
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
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PermitAll
    @Operation(summary = "Creates a new Customer", security = @SecurityRequirement(name = "apiKey"))
    public UserDto createUser(@RequestBody CreateUpdateUserDto userDto) {
        LOGGER.info("POST /api/v1/users body: {}", userDto);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")); // so admins can create other admin users
            return userMapper.userToUserDto(userService.registerUser(userDto, hasAdminRole));
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "forgot-password")
    @PermitAll
    @Operation(summary = "Lets a user create a password reset request", security = @SecurityRequirement(name = "apiKey"))
    public void forgotPassword(@Valid @RequestBody UserForgotPasswordDto data) {
        LOGGER.info("POST /api/v1/users/forgot-password body: {}", data);
        try {
            resetPasswordService.forgotPassword(data.email);
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "reset-password")
    @PermitAll
    @Operation(summary = "Lets a user reset their password", security = @SecurityRequirement(name = "apiKey"))
    public void resetPassword(@Valid @RequestBody UserResetPasswordDto data) {
        LOGGER.info("POST /api/v1/users/reset-password body: {}", data);
        try {
            resetPasswordService.resetPasswordFromHash(UUID.fromString(data.hash), data.password);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    @Secured("ROLE_USER")
    @Operation(summary = "Deletes the logged in Customer", security = @SecurityRequirement(name = "apiKey"))
    @Transactional
    public void deleteUser() {
        LOGGER.info("DELETE /api/v1/users");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        userService.deleteUser(user.getId());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{userId}")
    @Secured("ROLE_USER")
    @Operation(summary = "Updated an existing", security = @SecurityRequirement(name = "apiKey"))
    public void updateUser(@RequestBody CreateUpdateUserDto userDto, @PathVariable Long userId) {
        LOGGER.info("PUT /api/v1/users/{} body: {}", userId, userDto);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN")); // so admins can update themselves
            this.userService.updateUser(userDto, userId, hasAdminRole);
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        } catch (NotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/own")
    @Secured("ROLE_USER")
    @Operation(summary = "Get own userdata", security = @SecurityRequirement(name = "apiKey"))
    public UserDto getOwnUserData() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return userMapper.userToUserDto(userService.findApplicationUserByEmail(email));
        } catch (NotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
