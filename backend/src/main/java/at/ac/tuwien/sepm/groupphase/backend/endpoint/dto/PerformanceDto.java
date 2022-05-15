package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data transfer object of the performance entity.
 */
@Data
public class PerformanceDto {
    private long id;
    private LocalDateTime dateTime;
    private LocationDto location;
}
