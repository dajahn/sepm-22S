package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeatTicketDto extends TicketDto {

    private SeatSectorDto sector;
    private SeatDto seat;
}
