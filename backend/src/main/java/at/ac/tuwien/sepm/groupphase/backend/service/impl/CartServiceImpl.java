package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StandingSectorRepository standingSectorRepository;
    private final StandingTicketRepository standingTicketRepository;
    private final SeatTicketRepository seatTicketRepository;

    public CartServiceImpl(OrderRepository orderRepository, UserRepository userRepository, StandingSectorRepository standingSectorRepository, StandingTicketRepository standingTicketRepository, SeatTicketRepository seatTicketRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.standingSectorRepository = standingSectorRepository;
        this.standingTicketRepository = standingTicketRepository;
        this.seatTicketRepository = seatTicketRepository;
    }

    @Transactional
    @Override
    public void addTicketsToCart(List<CreateTicketDto> tickets) {
        LOGGER.debug("Add {} items to cart", tickets.size());
        TicketOrder cart = getCart();
        for (CreateTicketDto ticket : tickets) {
            switch (ticket.getType()) {
                case STANDING:
                    Optional<StandingSector> sector = standingSectorRepository.findById(ticket.getItem());

                    if (!sector.isPresent()) {
                        throw new ValidationException("Sector of standing ticket does not exist.");
                    }

                    if (standingTicketRepository.countByPerformanceIdAndStandingSectorId(ticket.getPerformance(), ticket.getItem()) >= sector.get().getCapacity()) {
                        throw new ValidationException("Not enough tickets left in standing sector.");
                    }

                    standingTicketRepository.save(new StandingTicket(ticket.getPerformance(), cart.getId(), ticket.getItem()));
                    break;
                case SEAT:
                    if (seatTicketRepository.existsByPerformanceIdAndSeatId(ticket.getPerformance(), ticket.getItem())) {
                        throw new ValidationException("Seat ticket is not available anymore.");
                    }

                    seatTicketRepository.save(new SeatTicket(ticket.getPerformance(), cart.getId(), ticket.getItem()));
                    break;
                default:
                    throw new NotImplementedException("Unexpected sector type.");
            }
        }
    }

    @Override
    public TicketOrder getCart() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOGGER.trace("Get cart of user with email '{}'", email);
        User user = userRepository.findUserByEmail(email);
        Optional<TicketOrder> databaseCart = orderRepository.findByTypeAndUserId(OrderType.CART, user.getId());

        if (databaseCart.isPresent()) {
            return databaseCart.get();
        }

        TicketOrder cart = new TicketOrder(LocalDateTime.now(), user.getId(), new ArrayList<>(), OrderType.CART);
        return orderRepository.save(cart);
    }
}
