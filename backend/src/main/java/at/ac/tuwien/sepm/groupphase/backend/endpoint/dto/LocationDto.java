package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

/**
 * Data transfer object of the location entity.
 */
@Data
public class LocationDto {

    private long id;
    private String name;
    private AddressDto address;
}
