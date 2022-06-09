package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

/**
 * Data transfer object of the point entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointDto {

    @SuppressWarnings("checkstyle:membername")
    private Integer x;

    @SuppressWarnings("checkstyle:membername")
    private Integer y;
}
