package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationSearchTermsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SearchLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SmallLocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/locations")
public class LocationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final LocationMapper locationMapper;
    private final LocationService locationService;

    public LocationEndpoint(LocationMapper locationMapper, LocationService locationService) {
        this.locationMapper = locationMapper;
        this.locationService = locationService;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get list of small locations (only id and name). If name is specified, only whose artists with matching name will come as a result.", security = @SecurityRequirement(name = "apiKey"))
    public List<SmallLocationDto> findAll(SearchLocationDto searchLocationDto) {
        LOGGER.info("GET /api/v1/locations with {}", searchLocationDto);
        return locationMapper.locationToSmallLocationDto(locationService.find(searchLocationDto));
    }

    @Secured("ROLE_USER")
    @GetMapping(value = "/search")
    @Operation(summary = "Get list of small locations (only id and name). If name is specified, only whose artists with matching name will come as a result.", security = @SecurityRequirement(name = "apiKey"))
    public List<LocationDto> findAllLocationsBy(LocationSearchTermsDto searchTermsDto) {
        LOGGER.info("GET /api/v1/locations with {}", searchTermsDto);
        return locationMapper.locationToLocationDto(locationService.findAllLocationsBy(searchTermsDto));
    }
}
