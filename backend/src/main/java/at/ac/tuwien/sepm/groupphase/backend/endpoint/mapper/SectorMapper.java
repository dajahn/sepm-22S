package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper
public interface SectorMapper {

    /**
     * This method is necessary because a Mapper cannot map sets to arrays out of the box.
     *
     * @param sectors the Sector Set that should be mapped
     * @return the mapped SectorDto Array
     */
    @IterableMapping
    default SectorDto[] sectorSetToSectorArray(Set<Sector> sectors) {
        SectorDto[] sectorDtos = new SectorDto[sectors.size()];
        int i = 0;

        for (Sector sector : sectors) {
            sectorDtos[i] = sectorToSectorDto(sector);
            i++;
        }

        return sectorDtos;
    }

    /**
     * This method is necessary because a Mapper cannot map abstract classes.
     * Uses dynamic binding to create the corresponding SectorDto for a Sector.
     *
     * @param sector the Sector that should be mapped
     * @return the mapped SectorDto
     */
    default SectorDto sectorToSectorDto(Sector sector) {
        return sector.mapToDto(this);
    }

    StandingSectorDto standingSectorToStandingSectorDto(StandingSector sector);

    SeatSectorDto seatSectorToStandingSectorDto(SeatSector sector);
}
