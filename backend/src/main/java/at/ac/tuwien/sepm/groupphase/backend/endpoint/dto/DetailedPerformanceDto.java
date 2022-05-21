package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Detailed data transfer object of the performance entity.
 */
@Data
public class DetailedPerformanceDto {

    private long id;
    private LocalDateTime dateTime;
    private DetailedLocationDto location;
    private EventDto event;
}
