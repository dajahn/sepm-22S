package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Detailed data transfer object of the event entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DetailedEventDto extends EventDto {

    private PerformanceDto[] performances;
}
