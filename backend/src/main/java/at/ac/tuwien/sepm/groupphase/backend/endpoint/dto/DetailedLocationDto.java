package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

/**
 * Detailed data transfer object of the location entity.
 */
@Data
public class DetailedLocationDto {

    private long id;
    private String name;
    private AddressDto address;
    private SectorDto[] sectors;
}
