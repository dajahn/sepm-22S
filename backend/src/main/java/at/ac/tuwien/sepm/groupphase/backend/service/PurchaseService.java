package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedTicketsDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;

import java.util.List;

public interface PurchaseService {

    /**
     * Gets all purchased tickets for upcoming events of the currently logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @return the purchased TicketOrder for upcoming events of the currently logged-in user
     */
    List<Ticket> getUpcomingPurchasedTickets(Long userId);

    /**
     * Gets all purchased tickets for past events of the currently logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @return the purchased TicketOrder for past events of the currently logged-in user
     */
    PagedTicketsDto getPastPurchasedTickets(Long userId, int page, int size);

    /**
     * Cancels a purchased ticket.
     *
     * @param userId    the ID of the User that wants to cancel the Ticket
     * @param ticketIds the IDs of the Tickets to be cancelled
     */
    void cancel(Long userId, List<Long> ticketIds);
}
