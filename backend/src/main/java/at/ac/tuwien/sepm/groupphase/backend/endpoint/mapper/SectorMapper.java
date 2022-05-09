package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.lang.invoke.MethodHandles;

@Mapper
public interface SectorMapper {

    SectorMapper INSTANCE = Mappers.getMapper(SectorMapper.class);

    default SectorDto sectorToSectorDto(Sector sector) {
        return sector.mapToDto();
    }

    StandingSectorDto standingSectorToStandingSectorDto(StandingSector sector);

    SeatSectorDto seatSectorToStandingSectorDto(SeatSector sector);
}
