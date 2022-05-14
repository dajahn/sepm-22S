package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.FileTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoDeserializer;
import at.ac.tuwien.sepm.groupphase.backend.util.FileDtoSerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements EventTestData, ArtistTestData, LocationTestData, FileTestData, AddressTestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private static ObjectMapper MAPPER;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArtistMapper artistMapper;

    private CreateEventDto createEventDto;

    @BeforeAll
    public static void  initMapper() {
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
    public void setCreateEventDto() throws FileNotFoundException {
        eventRepository.deleteAll();
        createEventDto = new CreateEventDto();

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
        for (int j = 0; j < STANDING_SEC_ROWS; j++) {
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
        SmallLocationDto smallLocationDto = new SmallLocationDto();
        smallLocationDto.setName(location.getName());
        smallLocationDto.setId(location.getId());

        //Generate event
        createEventDto.setName(EVENT_TEST_TITLE);
        createEventDto.setDescription(EVENT_TEST_DESCRIPTION);
        createEventDto.setDuration(EVENT_TEST_DURATION);
        createEventDto.setCategory(EVENT_CATEGORY);
        CreatePerformanceDto[] performances = new CreatePerformanceDto[NUMBER_OF_PERFORMANCES];
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            CreatePerformanceDto perf = new CreatePerformanceDto();
            perf.setLocation(smallLocationDto);
            perf.setDateTime(LocalDateTime.now().plusDays((i + 1) * 2));
            performances[i] = perf;
        }
        createEventDto.setPerformances(performances);

        ArtistDto[] artists = new ArtistDto[NUMBER_OF_ARTISTS];
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            ArtistDto a = artistMapper.artistToArtistDto(artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION)));
            artists[i] = a;
        }
        createEventDto.setArtists(artists);

        FileInputStream fis = new FileInputStream("src/test/resources/" + IMAGE_FILE_NAME);
        FileDto fileDto = new FileDto();
        fileDto.setType(new MediaType("image","jpeg"));
        String fileString = IOUtils.toString(fis, Charset.availableCharsets().get("UTF-8"));
        fileString = fileString.replace("\n", "");
        fileDto.setImageBase64(fileString);
        createEventDto.setThumbnail(fileDto);
    }

    @Test
    public void givenNothing_whenCreate_thenCreatedEventWithAllSetPropertiesPlusId()
        throws Exception {
        createEventDto.setName(EVENT_TEST_TITLE);

        String body = MAPPER.writeValueAsString(createEventDto);

        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto eventDto = MAPPER.readValue(response.getContentAsString(),
            EventDto.class);
        assertNotNull(eventDto.getId());
        assertAll(
            () -> assertEquals(createEventDto.getName(), eventDto.getName()),
            () -> assertEquals(createEventDto.getDescription(), eventDto.getDescription()),
            () -> assertEquals(createEventDto.getCategory(), eventDto.getCategory()),
            () -> assertEquals(createEventDto.getDuration(), eventDto.getDuration()),
            () -> assertEquals(createEventDto.getArtists().length, eventDto.getArtists().length)
        );
    }

    @Test
    public void givenNothing_whenPostPayloadInvalid_then422() throws Exception {
        createEventDto.setName("    ");
        String body = MAPPER.writeValueAsString(createEventDto);

        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus())
        );
    }


    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(EVENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus())
        );
    }

}
