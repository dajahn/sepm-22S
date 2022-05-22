package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.FileTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.UserTestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserRole;
import at.ac.tuwien.sepm.groupphase.backend.enums.UserStatus;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    private PerformanceRepository performanceRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CartService cartService;

    @Test
    public void givenNothing_whenCreatePurchaseOrders_thenFindPurchasedEvents() {
        eventRepository.deleteAll();
        orderRepository.deleteAll();
        locationRepository.deleteAll();
        artistRepository.deleteAll();
        userRepository.deleteAll();
        ticketRepository.deleteAll();
        performanceRepository.deleteAll();

        //generate location for event
        Location location = new Location();
        location.setName(LOCATION_NAME);

        Address address = new Address();
        address.setStreet(STREET);
        address.setZipCode(ZIP);
        address.setCity(CITY);
        address.setCountry(COUNTRY);
        location.setAddress(address);

        Set<Sector> sectors = new HashSet<>();
        List<StandingSector> sectorList = new ArrayList<>();
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
            sectorList.add(sector);
        }
        location.setSectors(sectors);
        location = locationRepository.save(location);

        //Generate Artists
        Set<Artist> artists = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            Artist a = artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION));
            artists.add(a);
        }

        //Generate event
        Event event = new Event();
        event.setName(EVENT_TEST_TITLE);
        event.setDescription(EVENT_TEST_DESCRIPTION);
        event.setDuration(EVENT_TEST_DURATION);
        event.setCategory(EVENT_CATEGORY);
        event.setThumbnail(null);
        event.setArtists(artists);
        event = eventRepository.save(event);


        //Generate Performances
        List<Performance> performances = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            Performance perf = new Performance();
            perf.setLocation(location);
            perf.setEvent(event);
            if (i % 2 == 0) {
                perf.setDateTime(LocalDateTime.now().plusDays(i + 1));
            } else {
                perf.setDateTime(LocalDateTime.now().minusDays(i + 1));
            }
            perf = performanceRepository.save(perf);
            performances.add(perf);
        }
        event.setPerformances(performances);

        //Generate Tickets
        List<Ticket> tickets = new ArrayList<>();
        StandingTicket ticket = new StandingTicket();
        ticket.setPerformance(performances.get(1));
        ticket.setPerformanceId(performances.get(1).getId());
        ticket.setSector(sectorList.get(1));
        ticket.setSectorId(sectorList.get(1).getId());
        ticket = ticketRepository.save(ticket);
        tickets.add(ticket);

        Faker faker = new Faker();

        User user = User.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName())
            .address(address).password("ABC").email(faker.name().firstName() + "." + faker.name().lastName() + '@')
            .role(UserRole.CUSTOMER).status(UserStatus.OK).readNews(new HashSet<>()).failedLoginAttempts(0).build();

        user = userRepository.save(user);


        TicketOrder order = new TicketOrder();
        order.setType(OrderType.PURCHASE);
        order.setDateTime(LocalDateTime.of(LocalDate.ofInstant(faker.date().future(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0)));
        order.setValidUntil(order.getDateTime().plusHours(1)); // VALID FOR 1 HOUR
        order.setUser(user);
        order.setUserId(user.getId());
        order.setTickets(tickets);
        order = orderRepository.save(order);
        ticket.setOrder(order);
        ticket.setOrderId(order.getId());

        User finalUser = user;

        assertAll(
            () -> assertEquals(NUMBER_OF_PERFORMANCES / 2, cartService.getPurchasedTickets(finalUser.getId(), true).size()), //getUpcomingTickets
            () -> assertEquals(NUMBER_OF_PERFORMANCES / 2, cartService.getPurchasedTickets(finalUser.getId(), false).size()) //getPastTickets
        );

    }
}
