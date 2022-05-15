package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;

import java.io.IOException;

public interface EventService {

    /**
     * Find a single event.
     *
     * @param id the id of the event
     * @return the event
     */
    Event findOne(Long id);

    /**
     * Inserts an event.
     *
     * @param eventDto Event to insert
     * @return The inserted event
     * @throws IOException if something goes wrong while saving image
     */
    Event createEvent(CreateEventDto eventDto) throws IOException;

}
