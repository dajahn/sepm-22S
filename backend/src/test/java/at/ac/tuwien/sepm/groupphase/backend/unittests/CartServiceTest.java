package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
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
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatSectorRepository seatSectorRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback
    @Transactional
    public void givenCart_whenAddTicketsToCart_thenCartIncreased() {
        // GIVEN
        User user = userRepository.findAll().get(0);
        int before = cartService.getCart(user.getId()).getTickets().size();

        // WHEN
        List<CreateTicketDto> tickets = new ArrayList<>();
        CreateTicketDto ticketDto = new CreateTicketDto();
        ticketDto.setType(SectorType.SEAT);
        ticketDto.setPerformance(1L);
        ticketDto.setItem(1L);
        tickets.add(ticketDto);
        cartService.addTicketsToCart(user.getId(), tickets);

        // THEN
        assertEquals(cartService.getCart(user.getId()).getTickets().size(), (before + 1));
    }

    @Test
    @Rollback
    @Transactional
    public void givenNotEmptyCart_whenRemoveTicket_thenCartDecreased() {
        // GIVEN
        User user = userRepository.findAll().get(0);
        TicketOrder cart = cartService.getCart(user.getId());
        if(cart.getTickets().isEmpty()) {
            orderRepository.delete(cart);
            cart = generateOrder(user, OrderType.CART, false, 5);
        }
        int before = cart.getTickets().size();
        assertTrue(before > 0);

        // WHEN
        cartService.removeTicket(cart.getUserId(), cart.getTickets().get(0).getId());

        // THEN
        assertEquals(cartService.getCart(user.getId()).getTickets().size(), (before - 1));
    }

    @Test
    @Rollback
    @Transactional
    public void givenTicketsInCart_whenGetCart_thenShowTicketsInCart() {
        // GIVEN
        User user = userRepository.findAll().get(0);
        orderRepository.delete(cartService.getCart(user.getId()));
        TicketOrder order = generateOrder(user, OrderType.CART, false, 5);

        // WHEN
        TicketOrder cart = cartService.getCart(user.getId());

        // THEN
        assertTrue(cart.getTickets().containsAll(order.getTickets()));
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
