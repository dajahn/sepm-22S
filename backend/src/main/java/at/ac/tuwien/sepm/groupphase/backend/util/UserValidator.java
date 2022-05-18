package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUpdateUserDto;
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
     * Validates a CreateUpdateUserDto.
     *
     * @param userDto   CreateUpdateUserDto to validate
     * @param adminRole defines whether the creating person has admin privileges or not
     * @throws ValidationException if the DTO is not valid
     */
    public void validateUser(CreateUpdateUserDto userDto, boolean adminRole) {
        LOGGER.trace("validateUser with {}", userDto);

        if (userDto == null) {
            throw new ValidationException("User must not be null!");
        }
        validateFirstName(userDto);
        validateLastName(userDto);
        validateEmail(userDto);
        if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
            throw new ValidationException("Email already in use!");
        }

        validatePassword(userDto);
        AddressValidator.validateAddress(userDto.getAddress());
        validateRole(userDto, adminRole);
    }


    /**
     * Validates a CreateUpdateUserDto.
     *
     * @param userDto   CreateUpdateUserDto to validate
     * @param adminRole defines whether the updating person has admin privileges or not
     * @throws ValidationException if the DTO is not valid
     */
    public void validateUser(CreateUpdateUserDto userDto, Long id, boolean adminRole) {
        LOGGER.trace("validateUser with {}", userDto);

        if (userDto == null) {
            throw new ValidationException("User must not be null!");
        }
        if (id == null) {
            throw new ValidationException("Users ID must not be null!");
        }
        validateFirstName(userDto);
        validateLastName(userDto);
        validateEmail(userDto);
        validatePassword(userDto);
        AddressValidator.validateAddress(userDto.getAddress());
        validateRole(userDto, adminRole);
    }

    private void validateFirstName(CreateUpdateUserDto userDto) {
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
    }

    private void validateLastName(CreateUpdateUserDto userDto) {
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
    }

    private void validateEmail(CreateUpdateUserDto userDto) {
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
    }

    private void validatePassword(CreateUpdateUserDto userDto) {
        //validate password
        if (userDto.getPassword() == null) {
            throw new ValidationException("Users Password must not be null!");
        }
        if (userDto.getPassword().trim().isEmpty()) {
            throw new ValidationException("Users Password must not be empty!");
        }
        if (userDto.getPassword().length() > 255) {
            throw new ValidationException("Users Password is too long!");
        }
        if (userDto.getPassword().length() < 8) {
            throw new ValidationException("Users Password must not be shorter than 8 characters!");
        }
    }

    private void validateRole(CreateUpdateUserDto userDto, boolean adminRole) {
        if (userDto.getRole() == null) {
            throw new ValidationException("Users Role must not be null!");
        }
        if (userDto.getRole().equals(UserRole.ADMIN) && !adminRole) {
            throw new ValidationException("Users Role must be Customer if not created by an admin!");
        }
        if ((!userDto.getRole().equals(UserRole.CUSTOMER) && (!userDto.getRole().equals(UserRole.ADMIN)))) {
            throw new ValidationException("Users Role must either be Admin or Customer!");
        }
    }

}
