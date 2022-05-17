package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedPerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/events")
public class EventEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventService eventService;
    private final PerformanceService performanceService;
    private final EventMapper eventMapper;
    private final PerformanceMapper performanceMapper;

    public EventEndpoint(EventService eventService, PerformanceService performanceService, EventMapper eventMapper, PerformanceMapper performanceMapper) {
        this.eventService = eventService;
        this.performanceService = performanceService;
        this.eventMapper = eventMapper;
        this.performanceMapper = performanceMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{eventId}")
    @Operation(summary = "Get information about a specific event", security = @SecurityRequirement(name = "apiKey"))
    public EventDto findById(@PathVariable Long eventId) {
        LOGGER.info("GET /api/v1/events/{}", eventId);
        return eventMapper.eventToEventDto(eventService.findOne(eventId));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/{eventId}/performances/{performanceId}")
    @Operation(summary = "Get information about a specific performance from an event", security = @SecurityRequirement(name = "apiKey"))
    public DetailedPerformanceDto findById(@PathVariable Long eventId, @PathVariable Long performanceId) {
        LOGGER.info("GET /api/v1/events/{}/performances/{}", eventId, performanceId);
        return performanceMapper.performanceToDetailedPerformanceDto(performanceService.findOne(eventId, performanceId));
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a new Event Entry", security = @SecurityRequirement(name = "apiKey"))
    public EventDto createEvent(@RequestBody CreateEventDto eventDto) throws IOException {
        LOGGER.info("POST /api/v1/events body: {}", eventDto);
        try {
            return eventMapper.eventToEventDto(eventService.createEvent(eventDto));
        } catch (ValidationException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

}
