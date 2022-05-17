package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;

import java.util.List;

public interface CartService {

    /**
     * Adds the given to the cart of the logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @param tickets the tickets that should be added to the cart
     */
    void addTicketsToCart(Long userId, List<CreateTicketDto> tickets);

    /**
     * Gets the cart of the currently logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @return the cart TicketOrder of the currently logged-in user
     */
    TicketOrder getCart(Long userId);

    /**
     * Removes a ticket (by its ID) from the cart of the logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @param ticketId the ID of the ticked which should be removed
     */
    void removeTicket(Long userId, Long ticketId);

    /**
     * Gets all purchased tickets for upcoming events of the currently logged-in user.
     *
     * @param userId the ID of the logged-in user
     * @return the purchased TicketOrder of the currently logged-in user
     */
    List<Ticket> getPurchasedTickets(Long userId);
}
