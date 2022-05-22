package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import lombok.Data;

@Data
public class LocationSearchTermsDto {
    private String name;
    private String street;
    private String zipCode;
    private String city;
    private Country country;
}
