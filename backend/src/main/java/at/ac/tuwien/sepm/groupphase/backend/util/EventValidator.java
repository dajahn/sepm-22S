package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;

@Component
public class EventValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistService artistService;
    private final LocationService locationService;
    private final ArtistMapper artistMapper;

    public EventValidator(ArtistService artistService, LocationService locationService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.locationService = locationService;
        this.artistMapper = artistMapper;
    }

    /**
     * Validates a CreateEventDto.
     *
     * @param createEventDto CreateEventDto to validate
     * @throws ValidationException if the DTO is not valid
     */
    public void validateEvent(CreateEventDto createEventDto) {
        LOGGER.trace("validateEvent with {}", createEventDto);

        if (createEventDto == null) {
            throw new ValidationException("Event must not be null!");
        }
        if (createEventDto.getName().trim().isEmpty()) {
            throw new ValidationException("Event Name must not be empty!");
        }
        if (createEventDto.getName().length() > 126) {
            throw new ValidationException("Event Name ist too long!");
        }
        if (createEventDto.getDescription() == null) {
            throw new ValidationException("Event Description must not be null!");
        }
        if (createEventDto.getDescription().trim().isEmpty()) {
            throw new ValidationException("Event Description most not be empty!");
        }

        if (createEventDto.getDescription().length() > 1022) {
            throw new ValidationException("Event Description ist too long!");
        }
        if (createEventDto.getCategory() == null) {
            throw new ValidationException("Event Category must not be null!");
        }
        if (createEventDto.getDuration() == null) {
            throw new ValidationException("Event Duration must not be null!");
        }

        //check if all artists exist
        Set<ArtistDto> artists = new HashSet<>();
        for (int i = 0; i < createEventDto.getArtists().length; i++) {
            try {
                artists.add(artistMapper.artistToArtistDto(artistService.findById(createEventDto.getArtists()[i].getId())));
            } catch (NotFoundException ex) {
                throw new ValidationException("Invalid Artist(s)!");
            } catch (IllegalArgumentException ex) {
                throw new ValidationException("Two or more Artists are the same!");
            }
        }
        createEventDto.setArtists(artists.toArray(new ArtistDto[0]));
        CreatePerformanceDto perf;
        for (int i = 0; i < createEventDto.getPerformances().length; i++) {
            perf = createEventDto.getPerformances()[i];
            try {
                locationService.findById(perf.getLocation().getId());
            } catch (NotFoundException ex) {
                throw new ValidationException("Invalid Location for Performance on " + (perf.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) + "!");
            }
            if (perf.getDateTime().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Date and time must not be before now!");
            }
        }

    }

}
