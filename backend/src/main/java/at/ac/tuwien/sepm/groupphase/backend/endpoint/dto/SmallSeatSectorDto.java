package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Less detailed data transfer object of the seat sector entity which does not contain the seats.
 */
@Data()
@EqualsAndHashCode(callSuper = true)
public class SmallSeatSectorDto extends SectorDto {

    private SeatType seatType;

    public SmallSeatSectorDto() {
        super(SectorType.SEAT);
    }
}
