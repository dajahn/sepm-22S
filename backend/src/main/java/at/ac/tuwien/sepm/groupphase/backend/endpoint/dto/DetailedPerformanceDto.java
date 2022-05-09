package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class DetailedPerformanceDto {

    private long id;
    private EventDto event;
    private LocalDateTime dateTime;
    private LocationDto location;

    @Data
    public static class EventDto {
        private long id;
        private String name;
        private String description;
        private Duration duration;
        private Artist[] artists;
        private EventCategory category;
    }
}
