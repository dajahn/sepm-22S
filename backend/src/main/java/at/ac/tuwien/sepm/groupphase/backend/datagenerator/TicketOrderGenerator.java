package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandingSectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile({"generateData"})
@Component
public class TicketOrderGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_PAST_PURCHASED_TICKETS = 7;
    private static final int NUMBER_OF_UPCOMING_PURCHASED_TICKETS = 3;
    private static final int NUMBER_OF_CART_TICKETS = 3;
    private static final int NUMBER_OF_RESERVATION_ORDERS = 2;
    private static final int NUMBER_OF_RESERVATION_TICKETS = 3;


    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatSectorRepository seatSectorRepository;
    private final StandingSectorRepository standingSectorRepository;

    public TicketOrderGenerator(OrderRepository orderRepository, UserRepository userRepository, PerformanceRepository performanceRepository, SeatSectorRepository seatSectorRepository,
                                StandingSectorRepository standingSectorRepository, EventDataGenerator eventDataGenerator, UserDataGenerator userDataGenerator) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
        this.seatSectorRepository = seatSectorRepository;
        this.standingSectorRepository = standingSectorRepository;
    }


    @PostConstruct
    private void generateOrders() {
        if (orderRepository.count() > 0) {
            LOGGER.debug("Orders already generated");
        } else {
            List<User> users = userRepository.findAll();
            List<Performance> performances = performanceRepository.findAll();

            LocalDateTime now = LocalDateTime.now();
            ArrayList<Ticket> pastTickets = new ArrayList<>();
            ArrayList<Ticket> futureTickets = new ArrayList<>();
            for (Performance performance : performances) {

                // Get all seat tickets
                List<SeatSector> seatSectors = seatSectorRepository.findByPerformanceId(performance.getId());
                for (SeatSector sector : seatSectors) {
                    for (Seat seat : sector.getSeats()) {
                        SeatTicket ticket = new SeatTicket();
                        ticket.setPerformanceId(performance.getId());
                        ticket.setPerformance(performance);
                        ticket.setSectorId(sector.getId());
                        ticket.setSeatId(seat.getId());
                        if (!performance.getDateTime().isAfter(now)) {
                            pastTickets.add(ticket);
                        } else {
                            futureTickets.add(ticket);
                        }
                    }
                }

                // Get all standing tickets
                List<StandingSector> standingSectors = standingSectorRepository.findByPerformanceId(performance.getId());
                for (StandingSector sector : standingSectors) {
                    for (int i = 0; i < sector.getCapacity(); i++) {
                        StandingTicket ticket = new StandingTicket();
                        ticket.setPerformanceId(performance.getId());
                        ticket.setPerformance(performance);
                        ticket.setSectorId(sector.getId());
                        if (!performance.getDateTime().isAfter(now)) {
                            pastTickets.add(ticket);
                        } else {
                            futureTickets.add(ticket);
                        }
                    }
                }
            }

            int totalFutureTickets = NUMBER_OF_CART_TICKETS + NUMBER_OF_UPCOMING_PURCHASED_TICKETS + (NUMBER_OF_RESERVATION_ORDERS * NUMBER_OF_RESERVATION_TICKETS);
            for (int u = 0; u < users.size() && NUMBER_OF_PAST_PURCHASED_TICKETS <= pastTickets.size() && totalFutureTickets <= futureTickets.size(); u++) {
                // # PURCHASED
                TicketOrder purchaseOrder = new TicketOrder();
                purchaseOrder.setType(OrderType.PURCHASE);
                purchaseOrder.setDateTime(now.minusYears(1));
                purchaseOrder.setValidUntil(purchaseOrder.getDateTime().plusHours(1));
                purchaseOrder.setUser(users.get(u));
                purchaseOrder.setUserId(users.get(u).getId());
                orderRepository.save(purchaseOrder);

                // ## Insert past purchased tickets
                List<Ticket> purchaseTickets = new ArrayList<>();
                for (int p = 0; p < NUMBER_OF_PAST_PURCHASED_TICKETS && NUMBER_OF_PAST_PURCHASED_TICKETS <= pastTickets.size(); p++) {
                    Ticket pastTicket = selectTicket(pastTickets);
                    pastTicket.setOrderId(purchaseOrder.getId());
                    purchaseTickets.add(pastTicket);
                }
                // ## Insert upcoming  purchased tickets
                for (int i = 0; i < NUMBER_OF_UPCOMING_PURCHASED_TICKETS && NUMBER_OF_UPCOMING_PURCHASED_TICKETS <= futureTickets.size(); i++) {
                    Ticket upcomingTicket = selectTicket(futureTickets);
                    upcomingTicket.setOrderId(purchaseOrder.getId());
                    purchaseTickets.add(upcomingTicket);
                }
                purchaseOrder.setTickets(purchaseTickets);
                orderRepository.save(purchaseOrder);
                LOGGER.debug("Saving purchased order {}", purchaseOrder);



                // # RESERVATION
                for (int r = 0; r < NUMBER_OF_RESERVATION_ORDERS && (NUMBER_OF_RESERVATION_ORDERS * NUMBER_OF_RESERVATION_TICKETS) <= futureTickets.size(); r++) {
                    TicketOrder reservationOrder = new TicketOrder();
                    reservationOrder.setType(OrderType.RESERVATION);
                    reservationOrder.setDateTime(now);
                    reservationOrder.setValidUntil(reservationOrder.getDateTime().plusHours(1));
                    reservationOrder.setUser(users.get(u));
                    reservationOrder.setUserId(users.get(u).getId());
                    orderRepository.save(reservationOrder);

                    List<Ticket> reservationTickets = new ArrayList<>();
                    for (int rt = 0; rt < NUMBER_OF_RESERVATION_TICKETS; rt++) {
                        Ticket reservationTicket = selectTicket(futureTickets);
                        reservationTicket.setOrderId(reservationOrder.getId());
                        reservationTickets.add(reservationTicket);
                    }
                    reservationOrder.setTickets(reservationTickets);
                    orderRepository.save(reservationOrder);
                    LOGGER.debug("Saving reservation order {}", reservationOrder);
                }



                // # CART
                TicketOrder cartOrder = new TicketOrder();
                cartOrder.setType(OrderType.CART);
                cartOrder.setDateTime(now);
                cartOrder.setValidUntil(cartOrder.getDateTime().plusHours(1));
                cartOrder.setUser(users.get(u));
                cartOrder.setUserId(users.get(u).getId());
                orderRepository.save(cartOrder);

                List<Ticket> cartTickets = new ArrayList<>();
                for (int c = 0; c < NUMBER_OF_CART_TICKETS && NUMBER_OF_CART_TICKETS <= futureTickets.size(); c++) {
                    Ticket cartTicket = selectTicket(futureTickets);
                    cartTicket.setOrderId(cartOrder.getId());
                    cartTickets.add(cartTicket);
                }
                cartOrder.setTickets(cartTickets);
                orderRepository.save(cartOrder);
                LOGGER.debug("Saving cart order {}", cartOrder);
            }
        }
    }

    private Ticket selectTicket(ArrayList<Ticket> tickets) {
        return tickets.remove(new Random().nextInt(tickets.size()));
    }

}