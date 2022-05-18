package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Validates a CreateUserDto.
     *
     * @param userDto   CreateUserDto to validate
     * @param adminRole defines whether the creating person has admin privileges or not
     * @throws ValidationException if the DTO is not valid
     */
    public void validateUser(CreateUserDto userDto, boolean adminRole) {
        LOGGER.trace("validateUser with {}", userDto);

        if (userDto == null) {
            throw new ValidationException("User must not be null!");
        }
        //validate first name
        if (userDto.getFirstName() == null) {
            throw new ValidationException("Users Firstname must not be null!");
        }
        if (userDto.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Users Firstname must not be empty!");
        }
        if (userDto.getFirstName().length() > 126) {
            throw new ValidationException("Users Firstname is too long!");
        }
        //validate last name
        if (userDto.getLastName() == null) {
            throw new ValidationException("Users Lastname must not be null!");
        }
        if (userDto.getLastName().trim().isEmpty()) {
            throw new ValidationException("Users Lastname must not be empty!");
        }
        if (userDto.getLastName().length() > 126) {
            throw new ValidationException("Users Lastname is too long!");
        }

        //validate email
        if (userDto.getEmail() == null) {
            throw new ValidationException("Users Email must not be null!");
        }
        if (userDto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Users Email must not be empty!");
        }
        if (userDto.getEmail().length() > 255) {
            throw new ValidationException("Users Email is too long!");
        }
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(userDto.getEmail());
        if (!mat.matches()) {
            throw new ValidationException("Users Email must be a valid email address!");
        }
        if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
            throw new ValidationException("Email already in use!");
        }

        this.validatePassword(userDto.getEmail());

        //validate address
        AddressValidator.validateAddress(userDto.getAddress());
        //validate user role
        if (userDto.getRole() == null) {
            throw new ValidationException("Users Role must not be null!");
        }
        if (userDto.getRole().equals(UserRole.ADMIN) && !adminRole) {
            throw new ValidationException("Users Role must be Customer if not created by an admin!");
        }
        if (!(userDto.getRole().equals(UserRole.CUSTOMER) && !(userDto.getRole().equals(UserRole.ADMIN)))) {
            throw new ValidationException("Users Role must either be Admin or Customer!");
        }
    }

    /**
     * Validates a Password.
     *
     * @param password the password being validated
     * @throws ValidationException if the password is not valid
     */
    public void validatePassword(String password) {
        //validate password
        if (password == null) {
            throw new ValidationException("Users Password must not be null!");
        }
        if (password.trim().isEmpty()) {
            throw new ValidationException("Users Password must not be empty!");
        }
        if (password.length() > 255) {
            throw new ValidationException("Users Password is too long!");
        }
        if (password.length() < 8) {
            throw new ValidationException("Users Password must not be shorter than 8 characters!");
        }
    }
}
