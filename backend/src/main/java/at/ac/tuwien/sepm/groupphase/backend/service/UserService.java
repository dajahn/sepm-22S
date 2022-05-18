package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
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
    User registerUser(CreateUserDto userDto, boolean adminRole);

    /**
     * Increases the failedLoginAttempts property by 1.
     *
     * @param userDto which failed the login attempt
     *
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
     * Unlocks a user by id.
     *
     * @param id id of the user
     * @return User object wich was unlocked
     */
    User unlockUserById(Long id);

    /**
     * Locks a user by id.
     *
     * @param id of the user
     * @param mail of the admin which executes the request
     * @return the user object
     */
    User lockUserById(Long id, String mail);

    /**
     * Gets all users with locked ones first.
     *
     * @return list of user
     */
    List<User> getUserOrderByLocked();
}
