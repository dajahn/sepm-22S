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
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingTicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReservationService;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public ReservationServiceImpl(OrderRepository orderRepository, StandingSectorRepository standingSectorRepository, SeatRepository seatRepository, StandingTicketRepository standingTicketRepository,
                                  SeatTicketRepository seatTicketRepository, PerformanceRepository performanceRepository, TicketRepository ticketRepository) {
        this.standingSectorRepository = standingSectorRepository;
        this.seatRepository = seatRepository;
        this.standingTicketRepository = standingTicketRepository;
        this.seatTicketRepository = seatTicketRepository;
        this.orderRepository = orderRepository;
        this.performanceRepository = performanceRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void reserveTickets(Long userId, List<CreateTicketDto> tickets) {
        LOGGER.trace("reserveTickets() for user " + userId);
        if (performanceRepository.findById(tickets.get(0).getPerformance()).isEmpty()) {
            throw new NotFoundException("Performance with id " + tickets.get(0).getPerformance() + " does not exist!");
        }
        LocalDateTime validUntil = performanceRepository.findById(tickets.get(0).getPerformance()).get().getDateTime();
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
        //TODO maybe send email notification for reservation to customer
    }

    @Override
    public List<Ticket> getReservedTickets(Long userId) {
        LOGGER.trace("getReservedTickets() for user " + userId);
        List<Ticket> tickets = new ArrayList<>();
        List<TicketOrder> ticketOrders = orderRepository.findTicketOrdersByTypeAndUserId(OrderType.RESERVATION, userId);
        for (TicketOrder t : ticketOrders) {
            tickets.addAll(t.getTickets());
        }
        return tickets;
    }

    @Override
    public void deleteReservation(Long userId, Long ticketId) {
        LOGGER.trace("deleteReservation() for user " + userId + " with ticket=" + ticketId);
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isEmpty()) {
            throw new NotFoundException("Ticket does not exist!");
        }
        Optional<TicketOrder> orderOptional = orderRepository.findTicketOrderByTicketsContains(ticket.get());
        if (orderOptional.isEmpty()) {
            throw new NotFoundException("Ticket with id " + ticketId + " not found!");
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
    public void deleteAll(Long userId) {
        LOGGER.trace("deleteAll() for user " + userId);
        this.orderRepository.deleteAllByTypeAndUserId(OrderType.RESERVATION, userId);
    }

}
