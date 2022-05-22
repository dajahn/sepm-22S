package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Data transfer object for creating performances.
 */
@Data
public class CreatePerformanceDto {
    private long id;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:SSZ")
    private LocalDateTime dateTime;
    private SmallLocationDto location;
}
