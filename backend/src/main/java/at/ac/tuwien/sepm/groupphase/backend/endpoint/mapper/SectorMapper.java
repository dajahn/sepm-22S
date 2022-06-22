package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallSeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.HashSet;
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
     * This method is necessary because a Mapper cannot map arrays to sets out of the box.
     *
     * @param sectorDtos the SectorDto Array that should be mapped
     * @return the mapped Sector Set
     */
    @IterableMapping
    default Set<Sector> sectorDtoArrayToSectorSet(SectorDto[] sectorDtos) {
        Set<Sector> sectors = new HashSet<>();
        for (SectorDto sectorDto : sectorDtos) {
            sectors.add(sectorDtoToSector(sectorDto));
        }

        return sectors;
    }

    /**
     * This method is necessary because a Mapper cannot map sets to arrays out of the box.
     *
     * @param sectors the Sector Set that should be mapped
     * @return the mapped small SectorDto Array
     */
    @IterableMapping(qualifiedByName = "smallSector")
    @Named("smallSector")
    default SectorDto[] sectorSetToSmallSectorArray(Set<Sector> sectors) {
        SectorDto[] sectorDtos = new SectorDto[sectors.size()];
        int i = 0;

        for (Sector sector : sectors) {
            sectorDtos[i] = sectorToSmallSectorDto(sector);
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

    /**
     * This method is necessary because a Mapper cannot map abstract classes.
     * Uses dynamic binding to create the corresponding Sector for a SectorDto.
     *
     * @param sectorDto the Sector that should be mapped
     * @return the mapped SectorD
     */
    default Sector sectorDtoToSector(SectorDto sectorDto) {
        return sectorDto.mapToEntity(this);
    }

    /**
     * This method is necessary because a Mapper cannot map abstract classes.
     * Uses dynamic binding to create the corresponding small SectorDto for a Sector.
     *
     * @param sector the Sector that should be mapped
     * @return the mapped small SectorDto
     */
    @Named("smallSector")
    default SectorDto sectorToSmallSectorDto(Sector sector) {
        return sector.mapToSmallDto(this);
    }

    StandingSectorDto standingSectorToStandingSectorDto(StandingSector sector);

    SeatSectorDto seatSectorToSeatSectorDto(SeatSector sector);

    @Named("smallSector")
    SmallSeatSectorDto seatSectorToSmallSeatSectorDto(SeatSector sector);

    StandingSector standingSectorDtoToStandingSector(StandingSectorDto sectorDto);

    SeatSector seatSectorDtoToSeatSector(SeatSectorDto sectorDto);
}
