package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;

import java.util.List;

public interface LocationService {
    /**
     * Returns a list of Location, filtered by search params.
     *
     * @param searchLocationDto parameters to filter by
     * @return A list of matching locations
     */
    List<Location> find(SearchLocationDto searchLocationDto);

    /**
     * Returns a Location with given id.
     *
     * @param id to look for
     * @return location with given id
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if no location with this id is found
     */
    Location findById(Long id);
}
