package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CartDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/cart")
public class CartEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public CartEndpoint(CartService cartService, OrderMapper orderMapper, UserService userService, TicketMapper ticketMapper) {
        this.cartService = cartService;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Add tickets to cart", security = @SecurityRequirement(name = "apiKey"))
    public void create(@Valid @NotNull @RequestBody List<CreateTicketDto> tickets) {
        LOGGER.info("POST /api/v1/cart body: {}", tickets);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        cartService.addTicketsToCart(user.getId(), tickets);
    }

    @Transactional(readOnly = true)
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get information about a the items in the currently logged in users cart", security = @SecurityRequirement(name = "apiKey"))
    public CartDto find() {
        LOGGER.info("GET /api/v1/cart");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        return orderMapper.orderToCartDto(cartService.getCart(user.getId()));
    }

    @Transactional
    @Secured("ROLE_USER")
    @DeleteMapping(value = "/tickets/{ticketId}")
    @Operation(summary = "Removes a ticket from the currently logged in user's cart", security = @SecurityRequirement(name = "apiKey"))
    public void removeFromCart(@PathVariable Long ticketId) {
        LOGGER.info("PATCH /api/v1/cart/tickets/{}", ticketId);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        cartService.removeTicket(user.getId(), ticketId);
    }

    @Transactional(readOnly = true)
    @Secured("ROLE_USER")
    @GetMapping(value = "/purchased")
    @Operation(summary = "Get summary of all upcoming purchased events", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketDto> findUpcomingPurchasedEvents(Boolean upcoming) {
        LOGGER.info("GET /api/v1/cart/purchased, upcoming? : {}", upcoming);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        LOGGER.info("userId: {}", user.getId());
        return ticketMapper.ticketToTicketDto(cartService.getPurchasedTickets(user.getId(), upcoming));
    }

}
