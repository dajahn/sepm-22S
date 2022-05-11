package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

/**
 * Data transfer object of the address entity.
 */
@Data
public class AddressDto {

    private String street;
    private String zipCode;
    private String city;
    private String country;
}
