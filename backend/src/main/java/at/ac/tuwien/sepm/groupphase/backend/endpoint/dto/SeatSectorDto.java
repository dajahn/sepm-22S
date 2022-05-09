package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = true)
public class SeatSectorDto extends SectorDto {

    private SeatType seatType;
    private SeatDto[] seats;

    public SeatSectorDto() {
        super(SectorType.SEAT_SECTOR);
    }
}
