package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;

import java.util.List;

public interface ReservationService {
    void reserveTickets(Long id, List<CreateTicketDto> tickets);

    List<Ticket> getReservedTickets(Long userId);
}
