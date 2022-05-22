package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.FileTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
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
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoDeserializer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoSerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CartEndpointTest implements EventTestData, ArtistTestData, LocationTestData, FileTestData, AddressTestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    private static ObjectMapper MAPPER;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void initMapper() {
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();
        SimpleModule serializer =
            new SimpleModule("FileDtoSerializer", new Version(1, 0, 0, null, null, null));
        serializer.addSerializer(FileDto.class, new FileDtoSerializer());

        SimpleModule deserializer =
            new SimpleModule("FileDtoDeserializer", new Version(1, 0, 0, null, null, null));
        deserializer.addDeserializer(FileDto.class, new FileDtoDeserializer());

        MAPPER.registerModule(deserializer);
        MAPPER.registerModule(serializer);
    }

    @BeforeEach
    public void createEventAndPurchaseTicketOrder() {
//        eventRepository.deleteAll();
//        orderRepository.deleteAll();
//        locationRepository.deleteAll();
//        artistRepository.deleteAll();
//        userRepository.deleteAll();
//        ticketRepository.deleteAll();
//        performanceRepository.deleteAll();

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
    }

    @Test
    public void givenNothing_whenCreateEventAndOrderPurchaseAndGetPurchasedTickets() throws Exception {
        MvcResult mvcResult = this.mockMvc
            .perform(get("/api/v1/cart/purchased").header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)).param("upcoming", "true"))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        List<TicketDto> upcomingTicketDtos = Arrays.asList(MAPPER.readValue(response.getContentAsString(), TicketDto[].class));

        MvcResult mvcResult2 = this.mockMvc
            .perform(get("/api/v1/cart/purchased").header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)).param("upcoming", "false"))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();
        List<TicketDto> pastTicketDtos = Arrays.asList(MAPPER.readValue(response2.getContentAsString(), TicketDto[].class));

        assertAll(
            () -> assertEquals(NUMBER_OF_PERFORMANCES / 2, upcomingTicketDtos.size()),
            () -> assertEquals(NUMBER_OF_PERFORMANCES / 2, pastTicketDtos.size())
        );
    }

}
