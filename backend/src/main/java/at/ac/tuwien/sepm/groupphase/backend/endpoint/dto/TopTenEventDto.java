package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Data;


@Data
public class TopTenEventDto {
    private Long id;
    private String name;
    private FileDto thumbnail;
    private EventCategory category;
    private int ticketCount;
}
