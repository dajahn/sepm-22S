package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationSearchTermsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.LocationValidator;
import at.ac.tuwien.sepm.groupphase.backend.util.SqlStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements at.ac.tuwien.sepm.groupphase.backend.service.LocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationRepository locationRepository;

    private LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public List<Location> find(SearchLocationDto searchLocationDto) {
        LOGGER.trace("Find with {}", searchLocationDto);
        if (searchLocationDto.name() == null && searchLocationDto.maxRecords() == null) {
            return locationRepository.findAll();
        } else if (searchLocationDto.name() != null && searchLocationDto.maxRecords() == null) {
            return locationRepository.findByNameContaining(searchLocationDto.name());
        } else if (searchLocationDto.name() == null) {
            Pageable topResults = PageRequest.of(0, searchLocationDto.maxRecords());
            return locationRepository.findAll(topResults).stream().toList();
        } else {
            Pageable topResults = PageRequest.of(0, searchLocationDto.maxRecords());
            return locationRepository.findByNameContaining(searchLocationDto.name(), topResults);
        }
    }

    @Override
    public Location findById(Long id) {
        LOGGER.trace("FindById with {}", id);
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent()) {
            return location.get();
        } else {
            throw new NotFoundException("Location with id " + id + " not found!");
        }

    }

    @Override
    public List<Location> findAllLocationsBy(LocationSearchTermsDto searchTermsDto) {
        SqlStringConverter converter = new SqlStringConverter();

        String name = converter.toSqlString(searchTermsDto.getName());
        String city = converter.toSqlString(searchTermsDto.getCity());
        String zipCode = searchTermsDto.getZipCode();
        String street = converter.toSqlString(searchTermsDto.getStreet());

        int country = -1;
        if (searchTermsDto.getCountry() != null) {
            country = searchTermsDto.getCountry().ordinal();
        }

        return locationRepository.findAllBy(name, city, country, zipCode, street);
    }

    @Override
    public Location createLocation(CreateLocationDto locationDto) {
        LOGGER.trace("createLocation with {}", locationDto);
        LocationValidator.validateLocation(locationDto);
        Location location = locationMapper.createLocationDtoToLocation(locationDto);
        return locationRepository.save(location);
    }

}
