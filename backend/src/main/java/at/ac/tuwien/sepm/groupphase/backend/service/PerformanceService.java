package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;

public interface PerformanceService {

    Performance findById(Long id);
}
