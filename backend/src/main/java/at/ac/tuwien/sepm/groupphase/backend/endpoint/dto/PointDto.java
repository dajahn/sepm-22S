package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import javax.persistence.Column;

/**
 * Data transfer object of the point entity.
 */
@Data
public class PointDto {

    @SuppressWarnings("checkstyle:membername")
    private Integer x;

    @SuppressWarnings("checkstyle:membername")
    private Integer y;
}
