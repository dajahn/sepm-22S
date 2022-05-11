package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

/**
 * Data transfer object of the artist entity.
 */
@Data
public class ArtistDto {

    private Long id;
    private String name;
    private String description;
}
