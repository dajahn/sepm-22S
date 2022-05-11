package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class LocationService implements at.ac.tuwien.sepm.groupphase.backend.service.LocationService {
    private final LocationRepository locationRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> find(SearchLocationDto searchLocationDto) {
        LOGGER.debug("Find locations with {}", searchLocationDto);
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
}
