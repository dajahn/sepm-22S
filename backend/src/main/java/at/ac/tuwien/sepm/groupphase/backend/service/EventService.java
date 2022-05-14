package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;

import java.io.IOException;

public interface EventService {

    /**
     * Inserts an event.
     *
     * @param eventDto Event to insert
     * @return The inserted event
     * @throws IOException if something goes wrong while saving image
     */
    Event createEvent(CreateEventDto eventDto) throws IOException;
}
