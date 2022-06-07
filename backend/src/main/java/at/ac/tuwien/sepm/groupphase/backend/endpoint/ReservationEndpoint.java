package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketOrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequestMapping(value = "/api/v1/reservation")
public class ReservationEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ReservationService reservationService;
    private final OrderMapper orderMapper;
    private final UserService userService;

    public ReservationEndpoint(ReservationService reservationService, OrderMapper orderMapper, UserService userService) {
        this.reservationService = reservationService;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    @Transactional
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    @Operation(summary = "Reserves all given tickets", security = @SecurityRequirement(name = "apiKey"))
    public void reserveTickets(@Valid @NotNull @RequestBody List<CreateTicketDto> tickets) {
        LOGGER.info("POST /api/v1/reservation with {}", tickets);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        reservationService.reserveTickets(user.getId(), tickets);
    }

    @Transactional
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/toCart")
    @Operation(summary = "Moves all reserved tickets to cart", security = @SecurityRequirement(name = "apiKey"))
    public void moveReservedTicketsToCart(@Valid @NotNull @RequestBody List<ReservationDto> tickets) {
        LOGGER.info("POST /api/v1/reservation with {}", tickets);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        reservationService.moveTicketsToCart(user.getId(), tickets);
    }

    @Transactional
    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{ticketId}")
    @Operation(summary = "Deletes reservation for a ticket", security = @SecurityRequirement(name = "apiKey"))
    public void deleteReservation(@PathVariable Long ticketId) {
        LOGGER.info("DELETE /api/v1/reservation/{} ", ticketId);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        reservationService.deleteReservation(user.getId(), ticketId);
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @Operation(summary = "Get all reserved tickets of user", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketOrderDto> getAllReservedTickets() {
        LOGGER.info("GET /api/v1/reservation");
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        return orderMapper.ticketOrdersToTicketOrderDtos(reservationService.getReservedTickets(user.getId()));
    }
}
