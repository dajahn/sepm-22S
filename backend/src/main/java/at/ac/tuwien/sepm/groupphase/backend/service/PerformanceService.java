package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;

public interface PerformanceService {

    /**
     * Find a single performance by id.
     *
     * @param id the id of the performance
     * @return the performance
     */
    Performance findById(Long id);
}
