package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository) {
        this.performanceRepository = performanceRepository;
    }

    @Override
    public Performance findOne(Long eventId, Long performanceId) {
        LOGGER.trace("findOne(Long eventId, Long performanceId) with eventId {} and performanceId {}", eventId, performanceId);
        Optional<Performance> performance = performanceRepository.findByEventIdAndId(eventId, performanceId);
        if (performance.isPresent()) {
            return performance.get();
        } else {
            throw new NotFoundException(String.format("Could not find performance with id %d of event %d", performanceId, eventId));
        }
    }

}
