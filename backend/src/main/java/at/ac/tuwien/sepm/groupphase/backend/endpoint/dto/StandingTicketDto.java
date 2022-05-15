package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StandingTicketDto extends TicketDto {

    private StandingSectorDto sector;
}
