package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.CheckoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CartService cartService;
    private final InvoiceService invoiceService;
    private final OrderRepository orderRepository;
    private final TicketPrintingService ticketPrintingService;

    public CheckoutServiceImpl(CartService cartService, InvoiceService invoiceService, OrderRepository orderRepository, TicketPrintingService ticketPrintingService) {
        this.cartService = cartService;
        this.invoiceService = invoiceService;
        this.orderRepository = orderRepository;
        this.ticketPrintingService = ticketPrintingService;
    }

    @Transactional
    @Override
    public void checkout(Long userId) {
        TicketOrder cart = cartService.getCart(userId);
        LOGGER.trace("checkout() for user " + userId + " with cart '{}'", cart);

        if (!cart.getTickets().isEmpty()) {
            cart.setType(OrderType.PURCHASE);
            orderRepository.save(cart);
            invoiceService.create(cart);
            ticketPrintingService.processOrder(cart);
        } else {
            LOGGER.trace("checkout() for user " + userId + ": Cart is empty!");
        }
    }
}
