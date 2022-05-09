package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;

public interface EventService {

    /**
     * @param eventDto Event to insert
     * @return The inserted event
     */
    Event createEvent(EventDto eventDto);
}
