package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.EventTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.FileTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreatePerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EventService;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class EventServiceTest implements EventTestData, LocationTestData, AddressTestData, ArtistTestData, FileTestData {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ArtistMapper artistMapper;

    @Test
    public void givenNothing_whenSaveEvent_thenFindListWithOneElementAndFindEventById() throws IOException {
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
        SmallLocationDto smallLocationDto = new SmallLocationDto();
        smallLocationDto.setName(location.getName());
        smallLocationDto.setId(location.getId());

        //Generate event
        CreateEventDto event = new CreateEventDto();
        event.setName(EVENT_TEST_TITLE);
        event.setDescription(EVENT_TEST_DESCRIPTION);
        event.setDuration(EVENT_TEST_DURATION);
        event.setCategory(EVENT_CATEGORY);
        CreatePerformanceDto[] performances = new CreatePerformanceDto[NUMBER_OF_PERFORMANCES];
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            CreatePerformanceDto perf = new CreatePerformanceDto();
            perf.setLocation(smallLocationDto);
            perf.setDateTime(LocalDateTime.now().plusDays((i + 1) * 2));
            performances[i] = perf;
        }
        event.setPerformances(performances);

        ArtistDto[] artists = new ArtistDto[NUMBER_OF_ARTISTS];
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            ArtistDto a = artistMapper.artistToArtistDto(artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION)));
            artists[i] = a;
        }
        event.setArtists(artists);

        FileInputStream fis = new FileInputStream("src/test/resources/" + IMAGE_FILE_NAME);
        FileDto fileDto = new FileDto();
        fileDto.setType(FILE_TYPE);
        String fileString = IOUtils.toString(fis, Charset.availableCharsets().get("UTF-8"));
        fileString = fileString.replace("\n", "");
        fileDto.setImageBase64(fileString);
        event.setThumbnail(fileDto);

        Event finalEvent = eventService.createEvent(event);
        assertAll(
            () -> assertEquals(1, eventRepository.findAll().size()),
            () -> assertNotNull(eventRepository.findById(finalEvent.getId()))
        );
    }

    @Test
    public void givenIncorrectInput_whenSaveEvent_thenThrowValidationException() throws IOException {
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
        SmallLocationDto smallLocationDto = new SmallLocationDto();
        smallLocationDto.setName(location.getName());
        smallLocationDto.setId(location.getId());

        //Generate event
        CreateEventDto event = new CreateEventDto();
        event.setName(null);
        event.setDescription(EVENT_TEST_DESCRIPTION);
        event.setDuration(EVENT_TEST_DURATION);
        event.setCategory(EVENT_CATEGORY);
        CreatePerformanceDto[] performances = new CreatePerformanceDto[NUMBER_OF_PERFORMANCES];
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            CreatePerformanceDto perf = new CreatePerformanceDto();
            perf.setLocation(smallLocationDto);
            perf.setDateTime(LocalDateTime.now().plusDays((i + 1) * 2));
            performances[i] = perf;
        }
        event.setPerformances(performances);

        ArtistDto[] artists = new ArtistDto[NUMBER_OF_ARTISTS];
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            ArtistDto a = artistMapper.artistToArtistDto(artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION)));
            artists[i] = a;
        }
        event.setArtists(artists);

        FileInputStream fis = new FileInputStream("src/test/resources/" + IMAGE_FILE_NAME);
        FileDto fileDto = new FileDto();
        fileDto.setType(FILE_TYPE);
        String fileString = IOUtils.toString(fis, Charset.availableCharsets().get("UTF-8"));
        fileString = fileString.replace("\n", "");
        fileDto.setImageBase64(fileString);
        event.setThumbnail(fileDto);

        //test name
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setName("  ");
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setName(EVENT_TEST_TITLE);

        //test description
        event.setDescription(null);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setDescription("");
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setDescription("    ");
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setDescription(EVENT_TEST_DESCRIPTION);

        //test category
        event.setCategory(null);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setCategory(EVENT_CATEGORY);

        //test duration
        event.setDuration(null);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        event.setDuration(EVENT_TEST_DURATION);

        //test performances
        performances[0].setDateTime(LocalDateTime.now().minusDays(2));
        event.setPerformances(performances);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        performances[0].setDateTime(LocalDateTime.now().plusDays(2));
        performances[0].getLocation().setId(-100L);
        event.setPerformances(performances);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
        performances[0].getLocation().setId(location.getId());

        //test artists
        artists[0].setId(-100L);
        event.setArtists(artists);
        assertThrows(
            ValidationException.class, () -> eventService.createEvent(event)
        );
    }

    @Test
    public void givenNothing_whenSaveEvent_thenFindTopTenEvents() throws Exception {
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
        SmallLocationDto smallLocationDto = new SmallLocationDto();
        smallLocationDto.setName(location.getName());
        smallLocationDto.setId(location.getId());

        //Generate event
        CreateEventDto event = new CreateEventDto();
        event.setName(EVENT_TEST_TITLE);
        event.setDescription(EVENT_TEST_DESCRIPTION);
        event.setDuration(EVENT_TEST_DURATION);
        event.setCategory(EVENT_CATEGORY);
        CreatePerformanceDto[] performances = new CreatePerformanceDto[NUMBER_OF_PERFORMANCES];
        for (int i = 0; i < NUMBER_OF_PERFORMANCES; i++) {
            CreatePerformanceDto perf = new CreatePerformanceDto();
            perf.setLocation(smallLocationDto);
            perf.setDateTime(LocalDateTime.now().plusDays((i + 1) * 2));
            performances[i] = perf;
        }
        event.setPerformances(performances);

        ArtistDto[] artists = new ArtistDto[NUMBER_OF_ARTISTS];
        for (int i = 0; i < NUMBER_OF_ARTISTS; i++) {
            ArtistDto a = artistMapper.artistToArtistDto(artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION)));
            artists[i] = a;
        }
        event.setArtists(artists);

        FileInputStream fis = new FileInputStream("src/test/resources/" + IMAGE_FILE_NAME);
        FileDto fileDto = new FileDto();
        fileDto.setType(FILE_TYPE);
        String fileString = IOUtils.toString(fis, Charset.availableCharsets().get("UTF-8"));
        fileString = fileString.replace("\n", "");
        fileDto.setImageBase64(fileString);
        event.setThumbnail(fileDto);

        Event finalEvent = eventService.createEvent(event);
        assertAll(
            () -> assertEquals(1, eventRepository.findAll().size()),
            () -> assertNotNull(eventRepository.findById(finalEvent.getId()))
        );
    }

}
