package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;

import java.util.List;

public interface TicketService {

    /**
     * Find all valid tickets of a performance.
     *
     * @param eventId       the id of the event in which to search for the performance containing the tickets
     * @param performanceId the id of the performance from which the tickets should be loaded
     * @return the tickets of the performance
     */
    List<Ticket> findByEventIdAndPerformanceId(Long eventId, Long performanceId);
}
