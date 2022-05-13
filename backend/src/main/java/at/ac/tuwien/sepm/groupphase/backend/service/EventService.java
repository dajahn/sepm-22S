package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;

public interface EventService {

    /**
     * Find a single event.
     *
     * @param id the id of the event
     * @return the event
     */
    Event findOne(Long id);
}
