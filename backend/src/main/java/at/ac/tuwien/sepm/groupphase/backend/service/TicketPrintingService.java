package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;

import java.util.List;

public interface TicketPrintingService {

    /**
     * Generates pdf tickets for all tickets of the order and sends them bundled as an email to the client.
     *
     * @param order the order which should be processed
     */
    void processOrder(TicketOrder order);

    /**
     * Generates a pdf for a given ticket.
     *
     * @param ticket the ticket which pdf should be generated
     * @return byte[] representing the ticket pdf
     */
    byte[] generateTicket(Ticket ticket);

    /**
     * Send the client an email including all ticket pdfs.
     *
     * @param order   the order which the client should be notified about
     * @param tickets a list of ticket pds, which get attached to the email
     */
    void sendNotification(TicketOrder order, List<byte[]> tickets);
}
