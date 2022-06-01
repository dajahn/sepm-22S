package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CartService cartService;
    private final OrderRepository orderRepository;

    public ReservationServiceImpl(CartService cartService, OrderRepository orderRepository) {
        this.cartService = cartService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void reserveTickets(Long userId) {
        LOGGER.trace("reserveTickets() for user " + userId);
        TicketOrder cart = cartService.getCart(userId);
        if (cart == null) {
            throw new NotFoundException("Cart for User ID " + userId + " not found!");
        }
        cart.setType(OrderType.RESERVATION);
        //TODO should i set the validUntil field for the tickets to some time or is this just needed for cart?
        orderRepository.save(cart);
        //TODO maybe send notification for reservation
    }

    @Override
    public List<Ticket> getReservedTickets(Long userId) {
        LOGGER.trace("getReservedTickets() for user " + userId);
        List<Ticket> tickets = new ArrayList<>();
        List<TicketOrder> ticketOrders =  orderRepository.findTicketOrdersByTypeAndUserId(OrderType.RESERVATION, userId);
        for (TicketOrder t: ticketOrders) {
            tickets.addAll(t.getTickets());
        }
        return tickets;
    }
}
