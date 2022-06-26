package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = SectorMapper.class)
public interface LocationMapper {

    List<SmallLocationDto> locationToSmallLocationDto(List<Location> locations);

    SmallLocationDto map(Location value);

    List<LocationDto> locationToLocationDto(List<Location> locations);

    Location createLocationDtoToLocation(CreateLocationDto locationDto);

    LocationDto locationEntityToLocationDto(Location location);
}
