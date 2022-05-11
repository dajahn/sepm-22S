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

    /**
     * This method is necessary because a Mapper cannot map abstract classes.
     * Uses dynamic binding to create the corresponding SectorDto for a Sector.
     *
     * @param sector the Sector that should be mapped
     * @return the mapped SectorDto
     */
    default SectorDto sectorToSectorDto(Sector sector) {
        return sector.mapToDto();
    }

    StandingSectorDto standingSectorToStandingSectorDto(StandingSector sector);

    SeatSectorDto seatSectorToStandingSectorDto(SeatSector sector);
}
