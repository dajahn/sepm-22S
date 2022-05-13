package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
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
    public void addTicketsToCart(Long userId, List<CreateTicketDto> tickets) {
        LOGGER.trace("addTicketsToCart(Long userId, List<CreateTicketDto> tickets) with userId {} and {} tickets", userId, tickets.size());
        TicketOrder cart = getCart(userId);
        Ticket ticket;

        for (CreateTicketDto ticketDto : tickets) {
            switch (ticketDto.getType()) {
                case STANDING -> {
                    Optional<StandingSector> sector = standingSectorRepository.findById(ticketDto.getItem());
                    if (!sector.isPresent()) {
                        throw new ValidationException("Sector of standing ticket does not exist.");
                    }
                    if (standingTicketRepository.countByPerformanceIdAndStandingSectorId(ticketDto.getPerformance(), ticketDto.getItem()) >= sector.get().getCapacity()) {
                        throw new ValidationException("Not enough tickets left in standing sector.");
                    }
                    ticket = standingTicketRepository.save(new StandingTicket(ticketDto.getPerformance(), cart.getId(), ticketDto.getItem()));
                }
                case SEAT -> {
                    if (seatTicketRepository.existsByPerformanceIdAndSeatId(ticketDto.getPerformance(), ticketDto.getItem())) {
                        throw new ValidationException("Seat ticket is not available anymore.");
                    }
                    ticket = seatTicketRepository.save(new SeatTicket(ticketDto.getPerformance(), cart.getId(), ticketDto.getItem()));
                }
                default -> throw new NotImplementedException("Unexpected sector type.");
            }

            cart.getTickets().add(ticket);
        }

        orderRepository.save(cart);
    }

    @Override
    public TicketOrder getCart(Long userId) {
        LOGGER.info("Long userId for userId {}", userId);
        Optional<TicketOrder> databaseCart = orderRepository.findByTypeAndUserId(OrderType.CART, userId);

        if (databaseCart.isPresent()) {
            return databaseCart.get();
        }

        TicketOrder cart = new TicketOrder(LocalDateTime.now(), userId, new ArrayList<>(), OrderType.CART);
        return orderRepository.save(cart);
    }
}
