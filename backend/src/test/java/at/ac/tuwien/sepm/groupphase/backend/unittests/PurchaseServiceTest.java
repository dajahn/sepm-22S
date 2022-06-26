package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedTicketsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SeatSectorRepository seatSectorRepository;

    @Autowired
    private SeatTicketRepository seatTicketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private TicketMapper ticketMapper;

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
        performanceRepository.saveAndFlush(ticket.getPerformance());

        // when
        ValidationException e = assertThrows(ValidationException.class, () ->
            cartService.addTicketsToCart(ticket.getOrder().getUserId(), Collections.singletonList(new CreateTicketDto(
                ticket.getPerformanceId(),
                SectorType.SEAT,
                ticket.getSeatId()
            )))
        );
        assertThat(e.getMessage()).contains("not available");
        purchaseService.cancel(ticket.getOrder().getUserId(), Collections.singletonList(ticket.getId()));

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
        purchaseService.cancel(ticket.getOrder().getUserId(), Collections.singletonList(ticket.getId()));

        // when
        ValidationException e = assertThrows(ValidationException.class, () ->
            purchaseService.cancel(ticket.getOrder().getUserId(), Collections.singletonList(ticket.getId()))
        );

        // then
        assertThat(e.getMessage()).contains("already").contains("cancelled");
    }

    @Test
    @Rollback
    @Transactional
    public void givenUpcomingPurchasedTickets_whenGetUpcomingPurchasedTickets_thenShowUpcomingPurchasedTickets() {
        // GIVEN
        User user = userRepository.findAll().get(0);
        TicketOrder order = generateOrder(user, OrderType.PURCHASE, false, 5);

        assertEquals(5, order.getTickets().size());

        // WHEN
        List<Ticket> upcoming = purchaseService.getUpcomingPurchasedTickets(user.getId());

        // THEN
        assertTrue(upcoming.containsAll(order.getTickets()));
    }

    @Test
    @Rollback
    @Transactional
    public void givenPastPurchasedTickets_whenGetPastPurchasedTickets_thenShowPastPurchasedTickets() {
        // GIVEN
        User user = userRepository.findAll().get(0);
        TicketOrder order = generateOrder(user, OrderType.PURCHASE, true, 6);

        List<TicketDto> tickets = order.getTickets().stream().map(ticketMapper::ticketToTicketDto).toList();

        // WHEN
        PagedTicketsDto past = purchaseService.getPastPurchasedTickets(user.getId(), 0, 6);

        // THEN
        assertTrue(past.getTickets().containsAll(tickets));
    }

    private TicketOrder generateOrder(User user, OrderType type, boolean past, int numberOfTickets) {
        LocalDateTime now = LocalDateTime.now();

        Performance performance = performanceRepository.findById(1L).get();
        performance.setDateTime((past) ? now.minusYears(1) : now.plusYears(1));
        performanceRepository.save(performance);

        TicketOrder order = new TicketOrder();
        order.setType(type);
        order.setDateTime(now);
        order.setValidUntil(now.plusHours(1));
        order.setUser(user);
        order.setUserId(user.getId());
        orderRepository.save(order);

        List<Ticket> tickets = new ArrayList<>();
        List<SeatSector> seatSectors = seatSectorRepository.findByPerformanceId(performance.getId());
        int counter = 0;
        for (SeatSector sector : seatSectors) {
            for (Seat seat : sector.getSeats()) {
                SeatTicket ticket = new SeatTicket();
                ticket.setPerformanceId(performance.getId());
                ticket.setPerformance(performance);
                ticket.setSectorId(sector.getId());
                ticket.setSeatId(seat.getId());
                ticket.setOrderId(order.getId());
                tickets.add(ticket);
                if (counter == numberOfTickets) {
                    break;
                } else {
                    counter++;
                }
            }
            if (counter == numberOfTickets) {
                break;
            }
        }

        order.setTickets(tickets.subList(0, numberOfTickets));
        orderRepository.save(order);

        return order;
    }

}
