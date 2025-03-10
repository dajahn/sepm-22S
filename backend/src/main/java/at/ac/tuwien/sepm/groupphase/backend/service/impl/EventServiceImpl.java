package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventSearchTermsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopTenEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.util.EventValidator;
import at.ac.tuwien.sepm.groupphase.backend.util.SqlStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final ArtistMapper artistMapper;
    private final FileService fileService;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventRepository eventRepository,
                            LocationService locationService, ArtistMapper artistMapper,
                            FileService fileService, EventValidator eventValidator, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.artistMapper = artistMapper;
        this.fileService = fileService;
        this.eventValidator = eventValidator;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event findOne(Long id) {
        LOGGER.trace("findOne(Long id) with id {}", id);
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new NotFoundException(String.format("Could not find event with id %d", id));
        }
    }


    @Override
    @Transactional
    public Event createEvent(CreateEventDto eventDto) throws IOException {
        LOGGER.trace("createEvent with {}", eventDto);
        eventValidator.validateEvent(eventDto);
        File file = this.fileService.create(eventDto.getThumbnail());
        Event event = this.mapFromCreateEventToEvent(eventDto, file);
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getByNameSubstring(EventSearchDto eventSearchDto) {
        LOGGER.trace("getByNameSubstring({})", eventSearchDto);

        if (eventSearchDto.getName() == null && eventSearchDto.getMaxRecords() == null) {
            return this.eventRepository.findAll();
        } else if (eventSearchDto.getName() != null && eventSearchDto.getMaxRecords() == null) {
            return this.eventRepository.findByNameContaining(eventSearchDto.getName());
        } else if (eventSearchDto.getName() == null) {
            Pageable topResults = PageRequest.of(0, eventSearchDto.getMaxRecords());
            return this.eventRepository.findAll(topResults).stream().toList();
        } else {
            Pageable topResults = PageRequest.of(0, eventSearchDto.getMaxRecords());
            return eventRepository.findByNameContaining(eventSearchDto.getName(), topResults);
        }
    }

    @Override
    public List<Event> findAllEventsBy(EventSearchTermsDto eventSearchTermsDto) {
        SqlStringConverter converter = new SqlStringConverter();

        String description = converter.toSqlString(eventSearchTermsDto.getDescription());

        String du = eventSearchTermsDto.getDuration();
        Duration minDuration = null;
        Duration maxDuration = null;
        if (du != null) {
            String hours = du.substring(0, 2);
            String minutes = du.substring(3, 5);
            String time = "PT" + hours + "H" + minutes + "M";
            minDuration = Duration.parse(time).minusMinutes(30);
            maxDuration = minDuration.plusMinutes(60);
        }

        String name = converter.toSqlString(eventSearchTermsDto.getName());
        int category = -1;
        if (eventSearchTermsDto.getCategory() != null) {
            category = eventSearchTermsDto.getCategory().ordinal();
        }

        return eventRepository.findAllBy(category, description, minDuration, maxDuration, name);
    }

    private Event mapFromCreateEventToEvent(CreateEventDto createEventDto, File file) {
        LOGGER.trace("mapFromCreateEventToEvent with createEventDto: {} and file: {}", createEventDto, file);
        Event evt = new Event();
        evt.setName(createEventDto.getName());
        evt.setDescription(createEventDto.getDescription());
        evt.setCategory(createEventDto.getCategory());
        Set<Artist> artists = new HashSet<>();
        for (int i = 0; i < createEventDto.getArtists().length; i++) {
            artists.add(artistMapper.artistDtoToArtist(createEventDto.getArtists()[i]));
        }
        evt.setArtists(artists);
        evt.setDuration(createEventDto.getDuration());
        evt.setThumbnail(file);
        CreatePerformanceDto perf;
        List<Performance> performances = new ArrayList<>();
        for (int i = 0; i < createEventDto.getPerformances().length; i++) {
            perf = createEventDto.getPerformances()[i];
            Location location = locationService.findById(perf.getLocation().getId());
            Performance p = new Performance(perf.getDateTime(), location, evt);
            performances.add(p);
        }
        evt.setPerformances(performances);
        return evt;
    }

    @Override
    public List<TopTenEventDto> topTenEventsByCategory(EventCategory category) {
        LOGGER.trace("FindTopTenEvents this month in category: {}", category);
        LocalDateTime from = LocalDateTime.now().with(firstDayOfMonth()).withHour(0).withMinute(0);
        LocalDateTime to = LocalDateTime.now().with(lastDayOfMonth()).withHour(23).withMinute(59);
        Pageable pageable = PageRequest.of(0, 10);

        List<Event> events = eventRepository.findTopTenByCategory(from, to, category, pageable);
        List<Integer> ticketCount = eventRepository.topTenEventsTicketCount(from, to, category, pageable);

        List<TopTenEventDto> topTenEventDtos = new ArrayList<>();

        if (!events.isEmpty()) {
            for (int i = 0; i < events.size(); i++) {
                topTenEventDtos.add(eventMapper.eventToTopTenEventDto(events.get(i), ticketCount.get(i)));
            }
        } else {
            throw new NotFoundException("Could not find events in this category");
        }
        return topTenEventDtos;
    }

}

