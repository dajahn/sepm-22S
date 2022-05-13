package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Profile("generateData")
@Component
public class LocationDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_LOCATIONS_TO_GENERATE = 25;
    private final LocationRepository locationRepository;

    public LocationDataGenerator(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    private void generateLocations() {
        if (locationRepository.count() > 0) {
            LOGGER.debug("Locations already generated");
        } else {
            LOGGER.debug("Generating {} location entries", NUMBER_OF_LOCATIONS_TO_GENERATE);
            Faker faker = new Faker();

            for (int i = 0;
                 i < NUMBER_OF_LOCATIONS_TO_GENERATE;
                 i++) {

                Location location = new Location();
                location.setName(faker.lordOfTheRings().location());

                Address address = new Address();
                address.setStreet(faker.address().streetName() + " " + faker.address().streetAddressNumber());
                address.setZipCode(faker.address().zipCode());
                address.setCity(faker.address().city());
                address.setCountry(Country.AT);
                location.setAddress(address);

                Set<Sector> sectors = new HashSet<>();
                for (int j = 0;
                     j < 2 + (i % 3);
                     j++) {
                    StandingSector sector = new StandingSector();
                    sector.setPrice(20d);
                    sector.setCapacity(10 * (j + 1));
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
                for (int j = 0;
                     j < 3;
                     j++) {
                    SeatSector sector = new SeatSector();
                    sector.setPrice(30d + 10d * (j % SeatType.values().length));
                    sector.setSeatType(SeatType.values()[j % SeatType.values().length]);
                    List<Seat> seats = new ArrayList<>();

                    for (int k = 0;
                         k < (2 + i) * 8;
                         k++) {
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

                LOGGER.debug("Saving location {}", location);
                locationRepository.save(location);
            }
        }
    }

}
