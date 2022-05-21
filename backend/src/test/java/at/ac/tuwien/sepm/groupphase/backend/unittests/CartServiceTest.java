package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.FileTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import com.github.javafaker.Faker;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class CartServiceTest implements EventTestData, LocationTestData, AddressTestData, ArtistTestData, FileTestData, UserTestData {

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ArtistMapper artistMapper;

    @Test
    public void givenNothing_whenCreatePurchaseOrders_thenFindPurchasedEvents() throws Exception {
        //generate location for upcomingEvent
        Location location = new Location();
        location.setName(LOCATION_NAME);

        Address address = new Address();
        address.setStreet(STREET);
        address.setZipCode(ZIP);
        address.setCity(CITY);
        address.setCountry(COUNTRY);
        location.setAddress(address);

        Set<Sector> sectors = new HashSet<>();
        for (int j = 0; j < STANDING_SEC_ROWS; j++) {
            StandingSector sector = new StandingSector();
            sector.setName(STANDING_SEC_NAME + j);
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

        //Generate upcoming Event
        Event upcomingEvent = new Event();
        upcomingEvent.setName(EVENT_TEST_TITLE);
        upcomingEvent.setDescription(EVENT_TEST_DESCRIPTION);
        upcomingEvent.setDuration(EVENT_TEST_DURATION);
        upcomingEvent.setCategory(EVENT_CATEGORY);
        List<Performance> upcomingPerformances = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            Performance perf = new Performance();
            perf.setLocation(location);
            perf.setDateTime(LocalDateTime.now().plusDays((i + 1) * 2));
            upcomingPerformances.add(perf);
        }
        upcomingEvent.setPerformances(upcomingPerformances);

        Set<Artist> artists = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            Artist a = artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION));
            artists.add(a);
        }
        upcomingEvent.setArtists(artists);

        //Generate past Event
        Event pastEvent = new Event();
        pastEvent.setName(EVENT_TEST_TITLE);
        pastEvent.setDescription(EVENT_TEST_DESCRIPTION);
        pastEvent.setDuration(EVENT_TEST_DURATION);
        pastEvent.setCategory(EVENT_CATEGORY);
        List<Performance> pastPerformances = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            Performance perf = new Performance();
            perf.setLocation(location);
            perf.setDateTime(LocalDateTime.now().minusDays((i + 1) * 2));
            pastPerformances.add(perf);
        }
        pastEvent.setPerformances(pastPerformances);
        pastEvent.setArtists(artists);

        upcomingEvent.setThumbnail(null);
        pastEvent.setThumbnail(null);
        eventRepository.save(pastEvent);
        eventRepository.save(upcomingEvent);

        Faker faker = new Faker();
        Address a = new Address(STREET, ZIP, CITY, COUNTRY);
        User u = User.builder().firstName(USER_FIRST_NAME).lastName(USER_LAST_NAME).email(USER_EMAIL).password(USER_PASSWORD).role(UserRole.CUSTOMER).status(UserStatus.OK).address(a).build();
        User x = userRepository.save(u);
        u.setId(x.getId());

        TicketOrder order = new TicketOrder();
        order.setType(OrderType.PURCHASE);
        order.setDateTime(LocalDateTime.of(LocalDate.ofInstant(faker.date().future(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0)));
        order.setValidUntil(order.getDateTime().plusHours(1)); // VALID FOR 1 HOUR
        order.setUser(u);
        order.setUserId(u.getId());
        orderRepository.save(order);

        assertAll(() -> assertEquals(1, eventRepository.findAll().size()), () -> assertNotNull(eventRepository.findById(finalEvent.getId())));
    }

}
