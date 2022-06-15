package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private SeatTicketRepository seatTicketRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PerformanceRepository performanceRepository;

    private SeatTicket purchasedSeatTicket;

    @BeforeEach
    public void getPurchasedSeatTicket() {
        for (SeatTicket seatTicket : seatTicketRepository.findAll()) {
            if (seatTicket.getOrder().getType() == OrderType.PURCHASE) {
                purchasedSeatTicket = seatTicket;
            }
        }
    }

    @Test
    @Rollback
    @Transactional
    public void givenPurchasedSeatTicket_whenCancel_thenCanAddToCartAgain() {
        // given
        SeatTicket ticket = purchasedSeatTicket;
        ticket.getPerformance().setDateTime(LocalDateTime.now().plusYears(1));
        performanceRepository.save(ticket.getPerformance());

        // when
        ValidationException e = assertThrows(ValidationException.class, () ->
            cartService.addTicketsToCart(ticket.getOrder().getUserId(), Collections.singletonList(new CreateTicketDto(
                ticket.getPerformanceId(),
                SectorType.SEAT,
                ticket.getSeatId()
            )))
        );
        assertThat(e.getMessage()).contains("not available");
        purchaseService.cancel(ticket.getOrder().getUserId(), ticket.getId());

        // then
        cartService.addTicketsToCart(ticket.getOrder().getUserId(), Collections.singletonList(new CreateTicketDto(
            ticket.getPerformanceId(),
            SectorType.SEAT,
            ticket.getSeatId()
        )));
    }

    @Test
    @Rollback
    @Transactional
    public void givenCancelledTicket_whenCancelAgain_thenThrowValidationException() {
        // given
        SeatTicket ticket = purchasedSeatTicket;
        ticket.getPerformance().setDateTime(LocalDateTime.now().plusYears(1));
        performanceRepository.save(ticket.getPerformance());
        purchaseService.cancel(ticket.getOrder().getUserId(), ticket.getId());

        // when
        ValidationException e = assertThrows(ValidationException.class, () ->
            purchaseService.cancel(ticket.getOrder().getUserId(), ticket.getId())
        );

        // then
        assertThat(e.getMessage()).contains("already").contains("cancelled");
    }

}
