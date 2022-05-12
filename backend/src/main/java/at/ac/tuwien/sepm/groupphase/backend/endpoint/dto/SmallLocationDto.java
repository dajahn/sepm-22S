package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object of the Small records, e.g. for searching.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class SmallLocationDto {
    private Long id;
    private String name;

}
