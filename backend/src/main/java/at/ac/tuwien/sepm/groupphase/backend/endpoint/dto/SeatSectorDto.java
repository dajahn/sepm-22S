package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object of the seat sector entity.
 */
@Data()
@EqualsAndHashCode(callSuper = true)
public class SeatSectorDto extends SectorDto {

    private SeatType seatType;
    private SeatDto[] seats;

    public SeatSectorDto() {
        super(SectorType.SEAT);
    }

    @Override
    public Sector mapToEntity(SectorMapper mapper) {
        return mapper.seatSectorDtoToSeatSector(this);
    }
}
