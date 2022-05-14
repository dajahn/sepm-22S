package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData;
import at.ac.tuwien.sepm.groupphase.backend.basetest.LocationTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.AddressTestData.STREET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class LocationTest implements LocationTestData, AddressTestData {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private LocationService locationService;


    @Test
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

        Location loc2 = locationService.findById(location.getId());

        assertEquals(location, loc2);
    }

    @Test
    public void givenNoMatchingLocationsInDb_whenFindById_throwNotFoundException() {
        assertThrows(NotFoundException.class, () -> locationService.findById(-100L));
    }

    @Test
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
            locationRepository.save(location);
        }

        SearchLocationDto searchLocationDto = new SearchLocationDto("LLLLLLLL", 10);
        List<Location> locations = locationService.find(searchLocationDto);
        assertEquals(3, locations.size());
        for (Location a : locations) {
            assertTrue(a.getName().contains("LLLLLLLLLL"));
        }
    }
}
