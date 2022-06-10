package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketOrderDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;

import java.util.List;

public interface ReservationService {
    /**
     * Reverses given tickets for user with given User Id.
     *
     * @param id      of user that wants to reserve the tickets
     * @param tickets that should be reserved
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException if tickets do not validate
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException   if user with given id does not exist
     */
    void reserveTickets(Long id, List<CreateTicketDto> tickets);

    /**
     * Returns all reserved tickets of a user.
     *
     * @param userId ID of user whose reversed tickets should be returned
     * @return A list of all reserved ticket of given user
     */
    List<TicketOrder> getReservedTickets(Long userId);

    /**
     * Delete a ticket reservation by id of a given user.
     *
     * @param userId  ID of user that wants to delete his reserved ticket
     * @param ticketId ID of ticket that should be deleted
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if order does not contain ticket or user does not have any reservations or ticket not found
     */
    void deleteReservation(Long userId, Long ticketId);

    /**
     * Delete a ticket reservation by id of a given user.
     *
     * @param userId  ID of user that wants to delete his reserved ticket
     * @param ticketId ID of ticket that should be deleted
     * @param orderId ID of order containing ticket that should be deleted
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if order does not contain ticket or user does not have any reservations or ticket not found
     */
    void deleteReservation(Long userId, Long orderId, Long ticketId);


    /**
     * Deletes all reservations and adds tickets to cart.
     *
     * @param userId ID of user that wants to add his reserved ticket into cart
     * @param tickets tickets to be added to cart
     */
    void moveTicketsToCart(Long userId, List<ReservationDto> tickets);
}
