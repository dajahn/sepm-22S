package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.CheckoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import at.ac.tuwien.sepm.groupphase.backend.util.CheckoutValidator;
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
    private final CheckoutValidator checkoutValidator;
    private final TicketPrintingService ticketPrintingService;

    public CheckoutServiceImpl(CartService cartService, InvoiceService invoiceService, OrderRepository orderRepository, CheckoutValidator checkoutValidator, TicketPrintingService ticketPrintingService) {
        this.cartService = cartService;
        this.invoiceService = invoiceService;
        this.orderRepository = orderRepository;
        this.checkoutValidator = checkoutValidator;
        this.ticketPrintingService = ticketPrintingService;
    }


    @Transactional
    @Override
    public void checkout(Long userId, CheckoutDto checkoutDto) {
        LOGGER.trace("checkout() for user " + userId + " with checkoutDto '{}'", checkoutDto);
        checkoutValidator.validateCheckout(checkoutDto);
        TicketOrder cart = cartService.getCart(userId);

        if (cart == null) {
            throw new NotFoundException("Cart for User ID " + userId + " not found!");
        }

        checkoutValidator.validateCart(cart);

        cart.setType(OrderType.PURCHASE);
        orderRepository.save(cart);
        
        ticketPrintingService.processOrder(cart);
        invoiceService.create(cart);
    }
}
