package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Data;

import java.time.Duration;

@Data
public class EventSearchTermsDto {
    private Long id;
    private String name;
    private String description;
    private EventCategory category;
    private String duration;
    private Long artistId;
}
