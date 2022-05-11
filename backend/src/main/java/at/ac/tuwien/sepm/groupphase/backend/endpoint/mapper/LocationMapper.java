package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper( LocationMapper.class );

    LocationDto locationToLocationDto(Location location);

    Location locationDtoToLocation(LocationDto locationDto);

    List<LocationDto> locationToLocationDto (List<Location> locations);
}
