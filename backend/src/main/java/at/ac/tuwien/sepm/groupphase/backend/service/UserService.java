package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    User findApplicationUserByEmail(String email);

    /**
     * Creates a new user.
     *
     * @param userDto   user to create
     * @param adminRole whether the person wo wants to create a new user, is an admin or not
     * @return created user
     */
    User registerUser(CreateUpdateUserDto userDto, boolean adminRole);

    /**
     * Updates an existing User.
     *
     * @param userDto   user to update
     * @param adminRole whether the person wo wants to update the user, is an admin or not
     * @return updated user
     */
    User updateUser(CreateUpdateUserDto userDto, Long id, boolean adminRole);

    /**
     * Deletes an existing User.
     *
     * @param id of the user to delete
     */
    void deleteUser(Long id);

    /**
     * Increases the failedLoginAttempts property by 1.
     *
     * @param userDto which failed the login attempt
     */
    void addFailedLoginAttemptToUser(UserLoginDto userDto);

    /**
     * Resets the failedLoginAttempts property of a user.
     *
     * @param userLoginDto user where the count is reseted
     */
    void resetFailedLoginAttemptsForUser(UserLoginDto userLoginDto);

    /**
     * Checks if a user with a given mail is blocked ATM.
     *
     * @param userDto of user which is beeing checked
     * @return userStatus
     */
    UserStatus getUserStatus(UserLoginDto userDto);

    /**
     * Gets all locked users in the system.
     *
     * @return List of locked users
     */
    List<User> getLockedUser();

    /**
     * Un/Locks a user by id.
     *
     * @param id of the user
     * @param locked wheter the user should be set to locked or unlocked
     * @param mail of the admin which executes the request
     */
    User updateLockingState(Long id, boolean locked, String mail);


    /**
     * Gets all users by a userSearchDto(Filter).
     *
     * @param userSearchDto the filter
     * @param page current page (pagination)
     * @param size of the page (pagination)
     * @return paged list of user with the total amount of users
     */
    PagedUserDto getUser(UserSearchDto userSearchDto, int page, int size);
}
