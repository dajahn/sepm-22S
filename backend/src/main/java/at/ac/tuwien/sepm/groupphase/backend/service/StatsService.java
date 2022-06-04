package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StatsDto;

public interface StatsService {

    /**
     * Provides total amount of events, tickets and news.
     *
     * @return StatsDto with the data
     */
    StatsDto getStats();
}
