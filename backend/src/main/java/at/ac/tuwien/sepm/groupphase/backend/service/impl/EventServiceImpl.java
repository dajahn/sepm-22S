package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import at.ac.tuwien.sepm.groupphase.backend.service.PerformanceService;
import at.ac.tuwien.sepm.groupphase.backend.util.EventValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements at.ac.tuwien.sepm.groupphase.backend.service.EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final ArtistMapper artistMapper;
    private final FileService fileService;
    private final EventValidator eventValidator;

    public EventServiceImpl(EventRepository eventRepository,
                            LocationService locationService, ArtistMapper artistMapper,
                            FileService fileService, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.artistMapper = artistMapper;
        this.fileService = fileService;
        this.eventValidator = eventValidator;
    }

    @Override
    public Event createEvent(CreateEventDto eventDto) throws IOException {
        LOGGER.trace("createEvent with {}", eventDto);
        eventValidator.validateEvent(eventDto);
        File file = this.fileService.create(eventDto.getThumbnail());
        Event event = this.mapFromCreateEventToEvent(eventDto, file);
        return eventRepository.save(event);
    }

    private Event mapFromCreateEventToEvent(CreateEventDto createEventDto, File file) {
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
}
