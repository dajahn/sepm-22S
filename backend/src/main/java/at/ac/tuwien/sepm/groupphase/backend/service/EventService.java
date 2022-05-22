package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventSearchTermsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopTenEventDto;
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

    List<Event> findAllEventsBy(EventSearchTermsDto eventSearchTermsDto);

    /**
     * Returns the Events witch match the name substring.
     *
     * @param eventSearchDto substring name of the event, and amount of maxRecords
     * @return List of matching Events
     */
    List<Event> getByNameSubstring(EventSearchDto eventSearchDto);

    /**
     * Finds the top ten events this month with the count of how many tickets were sold.
     *
     * @param category in which the top ten events should be
     * @return TopTenEventDto with event values and ticket count.
     */
    List<TopTenEventDto> topTenEventsByCategory(EventCategory category);

}
