package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;

public interface PerformanceService {

    /**
     * Find a single performance from an event.
     *
     * @param eventId the id of the event in which to search for the performance
     * @param performanceId the id of the performance
     * @return the performance
     */
    Performance findOne(Long eventId, Long performanceId);
}
