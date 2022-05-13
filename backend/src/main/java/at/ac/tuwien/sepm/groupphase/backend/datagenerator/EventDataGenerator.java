package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Profile("generateData")
@Component
public class EventDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_EVENTS_TO_GENERATE = 200;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ArtistRepository artistRepository;
    private final LocationDataGenerator locationDataGenerator;
    private final ArtistDataGenerator artistDataGenerator;

    public EventDataGenerator(EventRepository eventRepository, LocationRepository locationRepository, ArtistRepository artistRepository, LocationDataGenerator locationDataGenerator, ArtistDataGenerator artistDataGenerator) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.artistRepository = artistRepository;
        this.locationDataGenerator = locationDataGenerator;
        this.artistDataGenerator = artistDataGenerator;
    }

    @PostConstruct
    private void generateEvents() {
        if (eventRepository.count() > 0) {
            LOGGER.debug("Events already generated");
        } else {
            List<Location> locations = locationRepository.findAll();
            List<Artist> artists = artistRepository.findAll();

            for (int i = 0;
                 i < NUMBER_OF_EVENTS_TO_GENERATE;
                 i++) {
                Faker faker = new Faker();

                Event event = new Event();
                event.setName(faker.rockBand().name());
                event.setDescription(faker.lorem().paragraph());
                event.setCategory(EventCategory.values()[faker.random().nextInt(0, 1)]);
                event.setDuration(Duration.ofMinutes(faker.random().nextInt(60, 180)));

                Set<Artist> eventArtists = new HashSet<>();
                int artistNumber = faker.random().nextInt(1, 4);
                for (int j = 0;
                     j < artistNumber;
                     j++) {
                    eventArtists.add(artists.get(faker.random().nextInt(0, artists.size() - 1)));
                }
                event.setArtists(eventArtists);

                List<Performance> performances = new ArrayList<>();
                for (Location location : locations) {
                    if (faker.random().nextBoolean()) {
                        Performance performance = new Performance();
                        performance.setLocation(location);
                        performance.setDateTime(LocalDateTime.of(LocalDate.ofInstant(faker.date().future(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0)));
                        performance.setEvent(event);
                        performances.add(performance);
                    }
                }

                event.setPerformances(performances);
                LOGGER.debug("Saving event {}", event);
                eventRepository.save(event);
            }
        }
    }

}
