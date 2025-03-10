package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

/**
 * Data transfer object of the event entity.
 */
@Data
public class EventDto {

    private Long id;
    private String name;
    private String description;
    private Duration duration;
    private FileDto thumbnail;
    private EventCategory category;
    private ArtistDto[] artists;
}
