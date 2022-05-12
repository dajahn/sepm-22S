package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * Data transfer object for creating events.
 */
@Data
public class CreateEventDto {
    private String name;
    private String description;
    private Duration duration;
    //    private MultipartFile thumbnail; //TODO
    private EventCategory category;
    private ArtistDto[] artists;
    private CreatePerformanceDto[] performances;
}
