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
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Profile({"generateData"})
@Component
public class TicketOrderGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ORDERS_TO_GENERATE = 200;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatSectorRepository seatSectorRepository;
    private final StandingSectorRepository standingSectorRepository;
    private final EventDataGenerator eventDataGenerator;
    private final UserDataGenerator userDataGenerator;

    public TicketOrderGenerator(OrderRepository orderRepository, UserRepository userRepository, PerformanceRepository performanceRepository, SeatSectorRepository seatSectorRepository,
                                StandingSectorRepository standingSectorRepository, EventDataGenerator eventDataGenerator, UserDataGenerator userDataGenerator) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
        this.seatSectorRepository = seatSectorRepository;
        this.standingSectorRepository = standingSectorRepository;
        this.eventDataGenerator = eventDataGenerator;
        this.userDataGenerator = userDataGenerator;
    }


    @PostConstruct
    private void generateOrders() {
        if (orderRepository.count() > 0) {
            LOGGER.debug("Orders already generated");
        } else {

            List<User> users = userRepository.findAll();
            List<Performance> performances = performanceRepository.findAll();

            List<Ticket> tickets = new ArrayList<>();

            for (Performance performance : performances) {

                List<SeatSector> seatSectors = seatSectorRepository.findByPerformanceId(performance.getId());
                for (SeatSector sector : seatSectors) {

                    for (Seat seat : sector.getSeats()) {
                        SeatTicket ticket = new SeatTicket();

                        ticket.setPerformanceId(performance.getId());
                        ticket.setSectorId(sector.getId());
                        ticket.setSeatId(seat.getId());

                        tickets.add(ticket);
                    }
                }

                List<StandingSector> standingSectors = standingSectorRepository.findByPerformanceId(performance.getId());
                for (StandingSector sector : standingSectors) {

                    for (int i = 0; i < sector.getCapacity(); i++) {
                        StandingTicket ticket = new StandingTicket();

                        ticket.setPerformanceId(performance.getId());
                        ticket.setSectorId(sector.getId());
                    }
                }
            }

            for (int i = 0; i < users.size() && i < tickets.size(); i++) {
                Faker faker = new Faker();

                TicketOrder order = new TicketOrder();

                if (i < users.size() / 3) {
                    order.setType(OrderType.CART);
                } else if (i < (users.size() / 3) * 2) {
                    order.setType(OrderType.RESERVATION);
                } else {
                    order.setType(OrderType.PURCHASE);
                }

                order.setDateTime(LocalDateTime.of(LocalDate.ofInstant(faker.date().future(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0)));
                order.setValidUntil(order.getDateTime().plusHours(1)); // VALID FOR 1 HOUR
                order.setUser(users.get(i));
                order.setUserId(users.get(i).getId());
                orderRepository.save(order);

                TicketOrder order2 = new TicketOrder();

                order2.setType(OrderType.CART);
                order2.setDateTime(LocalDateTime.of(LocalDate.ofInstant(faker.date().future(365, TimeUnit.DAYS).toInstant(), TimeZone.getDefault().toZoneId()), LocalTime.of(faker.random().nextInt(0, 23), 0)));
                order2.setValidUntil(order2.getDateTime().plusHours(1)); // VALID FOR 1 HOUR
                order2.setUser(users.get(i));
                order2.setUserId(users.get(i).getId());
                orderRepository.save(order2);

                List<Ticket> orderTickets = new ArrayList<>();
                int numberOfTickets = faker.random().nextInt(2, 3);
                for (int j = 0; j < numberOfTickets; j++) {
                    Ticket ticket = tickets.remove((int) faker.random().nextInt(0, tickets.size() - 1));
                    ticket.setOrderId(order.getId());
                    orderTickets.add(ticket);
                }
                order.setTickets(orderTickets);

                LOGGER.debug("Saving order {}", order);
                orderRepository.save(order);
            }
        }
    }

}