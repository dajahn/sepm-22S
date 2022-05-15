package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateUserDto;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class AddressValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Validates a AddressDto.
     *
     * @param addressDto AddressDto to validate
     * @throws ValidationException if the DTO is not valid
     */
    public static void validateAddress(AddressDto addressDto) {
        LOGGER.trace("validateAddress with {}", addressDto);
        if (addressDto == null) {
            throw new ValidationException("Address must not be null!");
        }
        //validate street
        if (addressDto.getStreet() == null) {
            throw new ValidationException("Address' Street must not be null!");
        }
        if (addressDto.getStreet().trim().isEmpty()) {
            throw new ValidationException("Address' Street must not be empty!");
        }
        if (addressDto.getStreet().length() > 255) {
            throw new ValidationException("Address' Street is too long!");
        }
        //validate zip code
        if (addressDto.getZipCode() == null) {
            throw new ValidationException("Address' Zip Code must not be null!");
        }
        if (addressDto.getZipCode().trim().isEmpty()) {
            throw new ValidationException("Address' Zip Code must not be empty!");
        }
        if (addressDto.getZipCode().length() > 31) {
            throw new ValidationException("Address' Zip Code is too long!");
        }

        //validate City
        if (addressDto.getCity() == null) {
            throw new ValidationException("Address' City must not be null!");
        }
        if (addressDto.getCity().trim().isEmpty()) {
            throw new ValidationException("Address' City must not be empty!");
        }
        if (addressDto.getCity().length() > 127) {
            throw new ValidationException("Address' City is too long!");
        }
        //validate county
        if (addressDto.getCountry() == null) {
            throw new ValidationException("Address' Country must not be null!");
        }
        try {
            Country.valueOf(addressDto.getCountry());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Address' Country is invalid!");
        }
    }
}
