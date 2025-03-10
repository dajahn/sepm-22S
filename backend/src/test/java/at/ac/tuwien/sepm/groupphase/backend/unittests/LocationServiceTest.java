package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AddressDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PointDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class LocationServiceTest implements LocationTestData, AddressTestData {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private LocationService locationService;

    @Test
    @Transactional
    @Rollback
    public void givenLocationsInDb_whenFindById_thenFindLocation() {
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
            sector.setName(STANDING_SEC_NAME+j);
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

        Location loc2 = locationService.findById(location.getId());

        assertEquals(location, loc2);
    }

    @Test
    @Transactional
    @Rollback
    public void givenNoMatchingLocationsInDb_whenFindById_throwNotFoundException() {
        assertThrows(NotFoundException.class, () -> locationService.findById(-100L));
    }

    @Test
    @Transactional
    @Rollback
    public void givenLocationsInDb_whenSearchByName_thenFindListOfMatchingLocations() {
        for (int i = 0; i < 3; i++) {
            //generate location for event
            Location location = new Location();
            location.setName(LOCATION_NAME + i + "LLLLLLLLLL");

            Address address = new Address();
            address.setStreet(STREET);
            address.setZipCode(ZIP);
            address.setCity(CITY);
            address.setCountry(COUNTRY);
            location.setAddress(address);

            Set<Sector> sectors = new HashSet<>();
            for (int j = 0; j < STANDING_SEC_ROWS; j++) {
                StandingSector sector = new StandingSector();
                sector.setName(STANDING_SEC_NAME+j);
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
            locationRepository.save(location);
        }

        SearchLocationDto searchLocationDto = new SearchLocationDto("LLLLLLLL", 10);
        List<Location> locations = locationService.find(searchLocationDto);
        assertEquals(3, locations.size());
        for (Location a : locations) {
            assertTrue(a.getName().contains("LLLLLLLLLL"));
        }
    }


    @Test
    @Rollback
    @Transactional
    public void givenCreateLocationDto_whenCreateLocation_thenLocationCreated() {
        // GIVEN
        CreateLocationDto createLocationDto = new CreateLocationDto();
        createLocationDto.setName(LOCATION_NAME);

        AddressDto address = new AddressDto();
        address.setCountry(String.valueOf(COUNTRY));
        address.setStreet(STREET);
        address.setCity(CITY);
        address.setZipCode(ZIP);
        createLocationDto.setAddress(address);

        SectorDto[] sectorDtos = new SectorDto[1];
        StandingSectorDto standingSectorDto = new StandingSectorDto();
        standingSectorDto.setName("Sample Sector");
        standingSectorDto.setPrice(69F);
        standingSectorDto.setCapacity(420);
        standingSectorDto.setPoint1(new PointDto(0,0));
        standingSectorDto.setPoint2(new PointDto(1,1));
        sectorDtos[0] = standingSectorDto;

        createLocationDto.setSectors(sectorDtos);

        // WHEN
        Location savedLocation = locationService.createLocation(createLocationDto);

        // THEN
        Optional<Location> tmp = locationRepository.findById(savedLocation.getId());
        Location location = tmp.get();
        assertEquals(LOCATION_NAME, location.getName());
        assertEquals(address.getCountry(), location.getAddress().getCountry().toString());
        assertEquals(address.getStreet(), location.getAddress().getStreet());
        assertEquals(address.getCity(), location.getAddress().getCity());
        assertEquals(address.getZipCode(), location.getAddress().getZipCode());
        assertEquals(sectorDtos.length, location.getSectors().size());
    }

    @Test
    @Rollback
    @Transactional
    public void givenInvalidCreateLocationDto_whenCreateLocation_thenThrowValidationException() {
        // GIVEN
        CreateLocationDto createLocationDto = new CreateLocationDto();
        createLocationDto.setName(LOCATION_NAME);

        AddressDto address = new AddressDto();
        address.setCountry(String.valueOf(COUNTRY));
        address.setStreet(STREET);
        address.setCity(CITY);
        address.setZipCode(ZIP);
        createLocationDto.setAddress(address);

        SectorDto[] sectorDtos = new SectorDto[1];
        StandingSectorDto standingSectorDto = new StandingSectorDto();
        standingSectorDto.setName("Sample Sector");
        standingSectorDto.setPrice(69F);
        standingSectorDto.setCapacity(420);
        standingSectorDto.setPoint1(new PointDto(0,0));
        standingSectorDto.setPoint2(new PointDto(1,1));
        standingSectorDto.setId(1L);
        sectorDtos[0] = standingSectorDto;

        ValidationException vex;

        // WHEN / THEN
        // - invalid name
        createLocationDto.setName(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Location Name must not be null!", vex.getMessage());
        createLocationDto.setName("");
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Location Name must not be empty!", vex.getMessage());
        createLocationDto.setName("a".repeat(200));
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Location Name is too long!", vex.getMessage());
        createLocationDto.setName(LOCATION_NAME);

        // - address
        createLocationDto.setAddress(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address must not be null!", vex.getMessage());
        createLocationDto.setAddress(address);

        // -- street
        address.setStreet(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Street must not be null!", vex.getMessage());
        address.setStreet("");
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Street must not be empty!", vex.getMessage());
        address.setStreet("a".repeat(300));
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Street is too long!", vex.getMessage());
        address.setStreet(STREET);
        createLocationDto.setAddress(address);

        // -- zip code
        address.setZipCode(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Zip Code must not be null!", vex.getMessage());
        address.setZipCode("");
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Zip Code must not be empty!", vex.getMessage());
        address.setZipCode("a".repeat(200));
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Zip Code is too long!", vex.getMessage());
        address.setZipCode(ZIP);
        createLocationDto.setAddress(address);

        // -- city
        address.setCity(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' City must not be null!", vex.getMessage());
        address.setCity("");
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' City must not be empty!", vex.getMessage());
        address.setCity("a".repeat(200));
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' City is too long!", vex.getMessage());
        address.setCity(CITY);
        createLocationDto.setAddress(address);

        // -- country
        address.setCountry(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Country must not be null!", vex.getMessage());
        address.setCountry("test");
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Address' Country is invalid!", vex.getMessage());
        address.setCountry(COUNTRY.toString());
        createLocationDto.setAddress(address);

        // - sectors
        createLocationDto.setSectors(null);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Location Sectors must not be null!", vex.getMessage());
        SectorDto[] testSector = new SectorDto[0];
        createLocationDto.setSectors(testSector);
        vex = assertThrows(ValidationException.class, () -> locationService.createLocation(createLocationDto));
        assertEquals("Location Sectors must not be empty!", vex.getMessage());
    }
}
