package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CancellationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/purchases")
public class PurchaseEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PurchaseService purchaseService;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public PurchaseEndpoint(PurchaseService purchaseService, UserService userService, TicketMapper ticketMapper) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.ticketMapper = ticketMapper;
    }

    @Transactional(readOnly = true)
    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get summary of all upcoming purchased events", security = @SecurityRequirement(name = "apiKey"))
    public List<TicketDto> findPurchasedEvents(Boolean upcoming) {
        LOGGER.info("GET /api/v1/purchases, upcoming? : {}", upcoming);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        return ticketMapper.ticketsToTicketDtos(purchaseService.getPurchasedTickets(user.getId(), upcoming));
    }

    @Transactional
    @Secured("ROLE_USER")
    @PutMapping
    @Operation(summary = "Cancel a purchased Ticket", security = @SecurityRequirement(name = "apiKey"))
    public void patch(@Valid @NotNull @RequestBody CancellationDto cancellationDto) {
        LOGGER.info("GET /api/v1/purchases, cancelOrderDto = {}", cancellationDto);
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findApplicationUserByEmail(email);
        purchaseService.cancel(user.getId(), cancellationDto.getCancelTicket());
    }

}
