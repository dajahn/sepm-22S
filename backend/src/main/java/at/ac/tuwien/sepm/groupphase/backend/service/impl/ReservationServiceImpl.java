package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReservationDto;
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
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
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
public class ReservationServiceImpl implements ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OrderRepository orderRepository;
    private final StandingSectorRepository standingSectorRepository;
    private final SeatRepository seatRepository;
    private final StandingTicketRepository standingTicketRepository;
    private final SeatTicketRepository seatTicketRepository;
    private final PerformanceRepository performanceRepository;
    private final TicketRepository ticketRepository;
    private final CartService cartService;

    public ReservationServiceImpl(OrderRepository orderRepository, StandingSectorRepository standingSectorRepository, SeatRepository seatRepository, StandingTicketRepository standingTicketRepository,
                                  SeatTicketRepository seatTicketRepository, PerformanceRepository performanceRepository, TicketRepository ticketRepository, CartService cartService) {
        this.standingSectorRepository = standingSectorRepository;
        this.seatRepository = seatRepository;
        this.standingTicketRepository = standingTicketRepository;
        this.seatTicketRepository = seatTicketRepository;
        this.orderRepository = orderRepository;
        this.performanceRepository = performanceRepository;
        this.ticketRepository = ticketRepository;
        this.cartService = cartService;
    }

    @Override
    public void reserveTickets(Long userId, List<CreateTicketDto> tickets) {
        LOGGER.trace("reserveTickets() for user {} with tickets = {}", userId, tickets);
        if (performanceRepository.findById(tickets.get(0).getPerformance()).isEmpty()) {
            throw new NotFoundException("Performance with id " + tickets.get(0).getPerformance() + " does not exist!");
        }
        LocalDateTime validUntil = performanceRepository.findById(tickets.get(0).getPerformance()).get().getDateTime().minusMinutes(30);
        if (validUntil.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Cannot reserve tickets 30 minutes or earlier before an event.");
        }
        TicketOrder reservation = new TicketOrder(LocalDateTime.now(), userId, new ArrayList<>(), OrderType.RESERVATION, validUntil);
        reservation = orderRepository.save(reservation);
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
                    ticket = standingTicketRepository.save(new StandingTicket(ticketDto.getPerformance(), reservation.getId(), ticketDto.getItem()));
                }
                case SEAT -> {
                    Optional<Seat> seat = seatRepository.findById(ticketDto.getItem());
                    if (!seat.isPresent()) {
                        throw new ValidationException("Seat of ticket does not exist.");
                    }
                    if (seatTicketRepository.existsByPerformanceIdAndSeatId(ticketDto.getPerformance(), ticketDto.getItem())) {
                        throw new ValidationException("Seat ticket is not available anymore.");
                    }
                    ticket = seatTicketRepository.save(new SeatTicket(ticketDto.getPerformance(), reservation.getId(), seat.get().getSector().getId(), ticketDto.getItem()));
                }
                default -> throw new NotImplementedException("Unexpected sector type.");
            }
            reservation.getTickets().add(ticket);
        }
        orderRepository.save(reservation);
    }

    @Override
    public List<TicketOrder> getReservedTickets(Long userId) {
        LOGGER.trace("getReservedTickets() for user {}", userId);
        return orderRepository.findTicketOrdersByTypeAndUserId(OrderType.RESERVATION, userId);
    }

    @Override
    public void deleteReservation(Long userId, Long ticketId) {
        LOGGER.trace("deleteReservation() for user {} with ticket={}", userId, ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new NotFoundException("Ticket does not exist!");
        }
        Optional<TicketOrder> orderOptional = orderRepository.findTicketOrderByTicketsContains(ticket.get());
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("No Order containing this Ticket found!");
        }
        TicketOrder order = orderOptional.get();

        List<Ticket> tickets = order.getTickets();
        tickets.removeIf(t -> t.getId().equals(ticketId));

        orderRepository.save(order);
        ticketRepository.deleteById(ticketId);
        if (tickets.size() == 0) {
            orderRepository.deleteById(order.getId());
        }
    }

    @Override
    @Transactional
    public void deleteReservation(Long userId, Long orderId, Long ticketId) {
        LOGGER.trace("deleteReservation() for user {} with ticket={}", userId, ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new NotFoundException("Ticket does not exist!");
        }
        Optional<TicketOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("No Order containing this Ticket found!");
        }
        TicketOrder order = orderOptional.get();

        List<Ticket> tickets = order.getTickets();
        tickets.removeIf(t -> t.getId().equals(ticketId));

        orderRepository.save(order);
        ticketRepository.deleteById(ticketId);
        if (tickets.size() == 0) {
            orderRepository.deleteById(order.getId());
        }
    }

    @Override
    @Transactional
    public void moveTicketsToCart(Long userId, List<ReservationDto> tickets) {
        LOGGER.trace("moveTicketsToCart() for user {} with tickets {}", userId, tickets);
        List<CreateTicketDto> ticketDtos = new ArrayList<>();
        for (ReservationDto t : tickets) {
            CreateTicketDto tmp = new CreateTicketDto();
            tmp.setItem(t.getItem());
            tmp.setPerformance(t.getPerformance());
            tmp.setType(t.getType());
            this.deleteReservation(userId, t.getOrderId(), t.getTicketId());
            ticketDtos.add(tmp);
        }
        this.cartService.addTicketsToCart(userId, ticketDtos);
    }

}
