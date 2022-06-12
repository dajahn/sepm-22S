package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;

import java.util.List;

public interface PurchaseService {

    /**
     * Gets all purchased tickets for upcoming events of the currently logged-in user.
     *
     * @param userId   the ID of the logged-in user
     * @param upcoming true if Event of purchased Ticket should be in the future, wrong if in the past
     * @return the purchased TicketOrder of the currently logged-in user
     */
    List<Ticket> getPurchasedTickets(Long userId, Boolean upcoming);

    /**
     * Cancels a purchased ticket.
     *
     * @param userId   the ID of the User that wants to cancel the Ticket
     * @param ticketId the ID of the Ticket to be cancelled
     */
    void cancel(Long userId, Long ticketId);
}
