package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Profile("generateData")
@Component
public class EventDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public EventDataGenerator(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (eventRepository.count() > 0) {
            LOGGER.debug("Event already generated");
        } else if (locationRepository.count() == 0) {
            LOGGER.debug("Cannot create event without locations");
        } else {
            LOGGER.debug("Generating {} location entries", locationRepository.count());
            Event event = new Event();
            event.setName("Best event ever");
            event.setDescription("Cant get better than this");
            event.setCategory(EventCategory.CONCERT);
            event.setDuration(Duration.ofHours(2));

            List<Performance> performances = new ArrayList<>();
            for (Location location : locationRepository.findAll()) {
                Performance performance = new Performance();
                performance.setLocation(location);
                performance.setDateTime(LocalDateTime.now().plusYears(1));
                performance.setEvent(event);
                performances.add(performance);
            }

            event.setPerformances(performances);
            LOGGER.debug("Saving event {}", event);
            eventRepository.save(event);
        }
    }

}
