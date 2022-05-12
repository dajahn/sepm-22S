package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> find(SearchLocationDto searchLocationDto);

    Location findById(Long id);
}
