package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(uses = SectorMapper.class)
public interface TicketMapper {

    /**
     * This method is necessary because a Mapper cannot map abstract classes.
     * Uses dynamic binding to create the corresponding TicketDto for a Ticket.
     *
     * @param ticket the Ticket that should be mapped
     * @return the mapped TicketDto
     */
    default TicketDto ticketToTicketDto(Ticket ticket) {
        return ticket.mapToDto(this);
    }

    StandingTicketDto standingTicketToStandingTicketDto(StandingTicket ticket);

    SeatTicketDto seatTicketToStandingTicketDto(SeatTicket ticket);
}
