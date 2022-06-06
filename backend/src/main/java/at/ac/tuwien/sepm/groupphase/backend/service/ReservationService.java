package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
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
    List<Ticket> getReservedTickets(Long userId);

    /**
     * Delete a ticket reservation by id of a given user.
     *
     * @param userId  ID of user that wants to delete his reserved ticket
     * @param orderId ID of order that should be deleted
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException if order does not contain ticket or user does not have any reservations
     */
    void deleteReservation(Long userId, Long orderId);

    /**
     * Deletes all reservations of specific user.
     *
     * @param userId  ID of user that wants to delete all of his reserved tickets
     */
    void deleteAll(Long userId);

    void moveTicketsToCart(Long userId, List<CreateTicketDto> tickets);
}
