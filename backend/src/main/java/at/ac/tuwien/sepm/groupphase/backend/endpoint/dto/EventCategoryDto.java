package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Data;

/**
 * Data transfer object of the EventCategory.
 */
@Data
public class EventCategoryDto {
    private EventCategory category;
}
