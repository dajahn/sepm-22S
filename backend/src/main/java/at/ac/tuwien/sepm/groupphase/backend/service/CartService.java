package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;

import java.util.List;

public interface CartService {

    /**
     * Adds the given to the cart of the logged-in user.
     *
     * @param tickets the tickets that should be added to the cart
     */
    void addTicketsToCart(List<CreateTicketDto> tickets);

    /**
     * Gets the cart of the currently logged-in user.
     *
     * @return the cart TicketOrder of the currently logged-in user
     */
    TicketOrder getCart();
}
