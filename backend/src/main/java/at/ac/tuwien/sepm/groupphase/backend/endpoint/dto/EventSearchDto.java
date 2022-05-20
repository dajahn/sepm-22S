package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.util.Objects;

@Data
public final class EventSearchDto {
    private final String name;
    private final Integer maxRecords;
}
