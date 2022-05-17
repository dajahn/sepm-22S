package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final StandingSectorRepository standingSectorRepository;
    private final SeatRepository seatRepository;
    private final StandingTicketRepository standingTicketRepository;
    private final SeatTicketRepository seatTicketRepository;
    private final TicketRepository ticketRepository;

    public CartServiceImpl(
        OrderRepository orderRepository,
        StandingSectorRepository standingSectorRepository,
        SeatRepository seatRepository,
        StandingTicketRepository standingTicketRepository,
        SeatTicketRepository seatTicketRepository,
        TicketRepository ticketRepository) {
        this.orderRepository = orderRepository;
        this.standingSectorRepository = standingSectorRepository;
        this.seatRepository = seatRepository;
        this.standingTicketRepository = standingTicketRepository;
        this.seatTicketRepository = seatTicketRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @Override
    public void addTicketsToCart(Long userId, List<CreateTicketDto> tickets) {
        LOGGER.trace("addTicketsToCart(Long userId, List<CreateTicketDto> tickets) with userId {} and {} tickets", userId, tickets.size());
        TicketOrder cart = getCart(userId);
        cart.setValidUntil(LocalDateTime.now().plusMinutes(30));
        Ticket ticket;

        for (CreateTicketDto ticketDto : tickets) {
            switch (ticketDto.getType()) {
                case STANDING -> {
                    Optional<StandingSector> sector = standingSectorRepository.findById(ticketDto.getItem());
                    if (!sector.isPresent() || sector.get().getName() == null) {
                        throw new ValidationException("Sector of standing ticket does not exist.");
                    }
                    if (standingTicketRepository.countByPerformanceIdAndStandingSectorId(ticketDto.getPerformance(), ticketDto.getItem()) >= sector.get().getCapacity()) {
                        throw new ValidationException("Not enough tickets left in standing sector.");
                    }
                    ticket = standingTicketRepository.save(new StandingTicket(ticketDto.getPerformance(), cart.getId(), ticketDto.getItem()));
                }
                case SEAT -> {
                    Optional<Seat> seat = seatRepository.findById(ticketDto.getItem());
                    if (!seat.isPresent()) {
                        throw new ValidationException("Seat of ticket does not exist.");
                    }
                    if (seatTicketRepository.existsByPerformanceIdAndSeatId(ticketDto.getPerformance(), ticketDto.getItem())) {
                        throw new ValidationException("Seat ticket is not available anymore.");
                    }
                    ticket = seatTicketRepository.save(new SeatTicket(ticketDto.getPerformance(), cart.getId(), seat.get().getSector().getId(), ticketDto.getItem()));
                }
                default -> throw new NotImplementedException("Unexpected sector type.");
            }

            cart.getTickets().add(ticket);
        }

        orderRepository.save(cart);
    }

    @Override
    public TicketOrder getCart(Long userId) {
        LOGGER.trace("getCart(Long userId) for userId {}", userId);
        Optional<TicketOrder> databaseCart = orderRepository.findByTypeAndUserId(OrderType.CART, userId);
        TicketOrder cart;

        if (databaseCart.isPresent()) {
            cart = databaseCart.get();

            if (LocalDateTime.now().isBefore(cart.getValidUntil())) {
                return cart;
            }

            orderRepository.delete(cart);
        }

        cart = new TicketOrder(LocalDateTime.now(), userId, new ArrayList<>(), OrderType.CART, LocalDateTime.now().plusMinutes(30));
        return orderRepository.save(cart);
    }

    @Transactional
    @Override
    public void removeTicket(Long userId, Long ticketId) {
        LOGGER.trace("removeTicket(Long userId, Long ticketId) for userId {} and ticketId {}", userId, ticketId);

        TicketOrder cart = getCart(userId);
        List<Ticket> tickets = cart.getTickets();
        tickets.removeIf(t -> t.getId().equals(ticketId));

        orderRepository.save(cart);
        ticketRepository.deleteById(ticketId);
        if (tickets.size() == 0) {
            LOGGER.trace("removeTicket(Long userId, Long ticketId): Removed order {}", cart);
            orderRepository.delete(cart);
        }
    }

    @Override
    public List<Ticket> getPurchasedTickets(Long userId) {
        LOGGER.trace("getPurchasedTickets(Long userId) for user with id: {}", userId);
        List<TicketOrder> ticketOrders = orderRepository.findTicketOrdersByTypeAndUserId(OrderType.PURCHASE, userId);
        List<Ticket> purchasedTickets = new ArrayList<>();
        List<Ticket> upcomingPurchasedTickets = new ArrayList<>();

        if (!ticketOrders.isEmpty()) {
            for (int i = 0; i < ticketOrders.size(); i++) {
                if (!ticketOrders.get(i).getTickets().isEmpty()) {
                    purchasedTickets.addAll(ticketOrders.get(i).getTickets());
                }
            }

            for (int i = 0; i < purchasedTickets.size(); i++) {
                if (purchasedTickets.get(i).getPerformance().getDateTime().isAfter(LocalDateTime.now())) {
                    upcomingPurchasedTickets.add(purchasedTickets.get(i));
                }
            }

            return upcomingPurchasedTickets;
        } else {
            throw new NotFoundException(String.format("Could not find purchased Tickets form User with id %d", userId));
        }
    }

}
