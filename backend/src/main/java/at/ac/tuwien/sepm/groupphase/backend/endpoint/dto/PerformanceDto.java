package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

//TODO add location when merging
public record PerformanceDto(Long id, @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm") LocalDateTime dateTime, EventDto event, LocationDto location) {
}
