package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Cancellation;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CancellationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final CancellationRepository cancellationRepository;
    private final InvoiceService invoiceService;

    public PurchaseServiceImpl(
        OrderRepository orderRepository,
        TicketRepository ticketRepository,
        CancellationRepository cancellationRepository,
        InvoiceService invoiceService
    ) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.cancellationRepository = cancellationRepository;
        this.invoiceService = invoiceService;
    }

    @Override
    public List<Ticket> getPurchasedTickets(Long userId, Boolean upcoming) {
        LOGGER.trace("getPurchasedTickets(Long userId) for user with id: {}", userId);
        List<TicketOrder> ticketOrders = orderRepository.findTicketOrdersByTypeAndUserId(OrderType.PURCHASE, userId);
        List<Ticket> purchasedTickets = new ArrayList<>();
        List<Ticket> result = new ArrayList<>();
        if (!ticketOrders.isEmpty()) {
            for (TicketOrder ticketOrder : ticketOrders) {
                if (!ticketOrder.getTickets().isEmpty()) {
                    purchasedTickets.addAll(ticketOrder.getTickets());
                }
            }
            for (Ticket purchasedTicket : purchasedTickets) {
                if (purchasedTicket.getCancellation() == null) {
                    if (upcoming) {
                        if (purchasedTicket.getPerformance().getDateTime().isAfter(LocalDateTime.now())) {
                            result.add(purchasedTicket);
                        }
                    } else {
                        if (purchasedTicket.getPerformance().getDateTime().isBefore(LocalDateTime.now())) {
                            result.add(purchasedTicket);
                        }
                    }
                }
            }
            return result;
        } else {
            throw new NotFoundException(String.format("Could not find purchased Tickets form User with id %d", userId));
        }
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long ticketId) {
        LOGGER.trace("cancel(Long userId, Long ticketId) with userId = {} and ticketId = {}", userId, ticketId);
        Optional<Ticket> result = ticketRepository.findByOrderTypeAndOrderUserIdAndId(OrderType.PURCHASE, userId, ticketId);
        if (result.isEmpty()) {
            throw new NotFoundException("The ticket you wanted to cancel was not found.");
        }

        Ticket ticket = result.get();
        if (ticket.getCancellation() != null) {
            throw new ValidationException("The ticket has already been cancelled.");
        }

        if (LocalDateTime.now().isAfter(ticket.getPerformance().getDateTime())) {
            throw new ValidationException("The performance is already over, the ticket cannot be cancelled.");
        }

        Cancellation cancellation = new Cancellation(LocalDateTime.now(), ticket.getId());
        ticket.setCancellation(cancellation);
        ticketRepository.save(ticket);
        cancellation = cancellationRepository.save(cancellation);
        cancellation.setTicket(ticket);
        invoiceService.create(cancellation);
    }

}
