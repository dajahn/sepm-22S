package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class LocationValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Validates a CreateLocationDto.
     *
     * @param locationDto CreateLocationDto to validate
     * @throws ValidationException if the DTO is not valid
     */
    public static void validateLocation(CreateLocationDto locationDto) {
        LOGGER.trace("validateLocation with {}", locationDto);

        if (locationDto == null) {
            throw new ValidationException("Location must not be null!");
        }
        if (locationDto.getName() == null) {
            throw new ValidationException("Location Name must not be null!");
        }
        if (locationDto.getName().trim().isEmpty()) {
            throw new ValidationException("Location Name must not be empty!");
        }
        if (locationDto.getName().length() > 126) {
            throw new ValidationException("Location Name is too long!");
        }
        AddressValidator.validateAddress(locationDto.getAddress());
        if (locationDto.getSectors() == null) {
            throw new ValidationException("Location Sectors must not be null!");
        }
        if (locationDto.getSectors().length == 0) {
            throw new ValidationException("Location Sectors must not be empty!");
        }
    }
}
