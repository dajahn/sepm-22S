package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallLocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SmallLocationMapper {
    SmallLocationMapper INSTANCE = Mappers.getMapper(SmallLocationMapper.class);


    List<SmallLocationDto> locationToSmallLocationDto(List<Location> locations);

    SmallLocationDto map(Location value);
}
