package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;

import java.io.IOException;
import java.util.List;

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

    /**
     * Finds the top ten sold events this month.
     *
     * @param category in which the top ten events should be
     * @return top ten events this month by sold tickets
     */
    List<Event> topTenEventsByCategory(EventCategory category);


    /**
     * Finds the number of how many tickets were sold in each top ten event.
     *
     * @param category in which the top ten events should be
     * @return number of sold tickets for top ten events
     */
    List<Integer> topTenEventsTicketCount(EventCategory category);

}
