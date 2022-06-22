package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

/**
 * Data transfer object for creating locations.
 */
@Data
public class CreateLocationDto {
    private String name;
    private AddressDto address;
    private SectorDto[] sectors;
}
