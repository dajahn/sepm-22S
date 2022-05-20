package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchTermsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepm.groupphase.backend.util.SqlStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PerformanceRepository performanceRepository;
    private final EventMapper eventMapper;

    public PerformanceServiceImpl(PerformanceRepository performanceRepository, EventMapper eventMapper) {
        this.performanceRepository = performanceRepository;
        this.eventMapper = eventMapper;
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

    @Override
    public List<Performance> findAllPerformancesBy(PerformanceSearchTermsDto searchTerms) {
        LOGGER.trace("findAllPerformancesBy(PerformanceSearchTermsDto) with searchTerms: {}", searchTerms);

        double fromPrice = 0;
        double toPrice = 0;
        if (searchTerms.getPrice() != 0) {
            fromPrice = searchTerms.getPrice() - 10;
            toPrice = searchTerms.getPrice() + 10;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

        String from = searchTerms.getFromDate();
        LocalDateTime fromDate = null;
        if (from != null) {
            LocalDate localDateFrom = LocalDate.parse(from.substring(8, 10) + "/" + from.substring(5, 7) + "/" + from.substring(0, 4), formatter);
            fromDate = LocalDateTime.of(localDateFrom, LocalTime.MIN);
        }

        String to = searchTerms.getToDate();
        LocalDateTime toDate = null;
        if (to != null) {
            LocalDate localDateTo = LocalDate.parse(to.substring(8, 10) + "/" + to.substring(5, 7) + "/" + to.substring(0, 4), formatter);
            toDate = LocalDateTime.of(localDateTo, LocalTime.MAX);
        }

        SqlStringConverter converter = new SqlStringConverter();
        String eventName = converter.toSqlString(searchTerms.getEventName());
        String locationName = converter.toSqlString(searchTerms.getLocationName());

        return performanceRepository.findAllBy(/*fromPrice, toPrice,*/ eventName , locationName,  fromDate, toDate);

    }

}
