package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class EventRepositoryTest implements EventTestData, LocationTestData, AddressTestData, ArtistTestData {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Test
    public void givenNothing_whenSaveEvent_thenFindListWithOneElementAndFindEventById() {
        //generate location for event
        Location location = new Location();
        location.setName(LOCATION_NAME);

        Address address = new Address();
        address.setStreet(STREET);
        address.setZipCode(ZIP);
        address.setCity(CITY);
        address.setCountry(COUNTRY);
        location.setAddress(address);

        List<Sector> sectors = new ArrayList<>();
        for (int j = 0; j < STANDING_SEC_ROWS ; j++) {
            StandingSector sector = new StandingSector();
            sector.setPrice(STANDING_SEC_PRICE);
            sector.setCapacity(STANDING_SEC_CAPACITY);
            Point point = new Point();
            point.setX(j * 8);
            point.setY(0);
            sector.setPoint1(point);
            point = new Point();
            point.setX(8 + j * 8);
            point.setY(4);
            sector.setPoint2(point);
            sector.setLocation(location);
            sectors.add(sector);
        }
        for (int j = 0; j < SEAT_SEC_ROWS; j++) {
            SeatSector sector = new SeatSector();
            sector.setPrice(SEAT_SEC_PRICE);
            sector.setSeatType(SeatType.values()[j]);
            List<Seat> seats = new ArrayList<>();

            for (int k = 0; k < SEAT_SEC_ROWS * SEA_SEC_COLS; k++) {
                Point point = new Point();
                point.setX(k);
                point.setY(5 + j);
                Seat seat = new Seat();
                seat.setRow(j + 1);
                seat.setColumn(k + 1);
                seat.setPoint(point);
                seats.add(seat);
            }
            sector.setSeats(seats);
            sector.setLocation(location);
            sectors.add(sector);
        }
        location.setSectors(sectors);
        location = locationRepository.save(location);

        //Generate event
        Event event = new Event();
        event.setName(EVENT_TEST_TITLE);
        event.setDescription(EVENT_TEST_DESCRIPTION);
        event.setDuration(EVENT_TEST_DURATION);
        event.setCategory(EVENT_CATEGORY);
        List<Performance> performances = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            Performance perf = new Performance(LocalDateTime.now().plusDays((i+1)*2), location, event);
            performances.add(perf);
        }
        event.setPerformances(performances);

        Set<Artist> artists = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            Artist a = artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION));
            artists.add(a);
        }
        event.setArtists(artists);
        event.setThumbnail(null);

        event = eventRepository.save(event);

        Event finalEvent = event;
        assertAll(
            () -> assertEquals(1, eventRepository.findAll().size()),
            () -> assertNotNull(eventRepository.findById(finalEvent.getId()))
        );

    }
}
