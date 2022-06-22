package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<TicketOrder, Long> {

    /**
     * Find one order of a user by their ID and the order type.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     * @return the order uf the user
     */
    Optional<TicketOrder> findFirstByTypeAndUserIdOrderByValidUntil(OrderType type, long userId);

    /**
     * Delete all orders of a certain type made by a user.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     */
    void deleteAllByTypeAndUserId(OrderType type, long userId);

    /**
     * Find all orders of a user by their ID and the order type.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     * @return all orders of the user
     */
    List<TicketOrder> findTicketOrdersByTypeAndUserId(OrderType type, long userId);

    /**
     * Delete all orders made by a user.
     *
     * @param userId the ID of the user
     */
    void deleteAllByUserId(long userId);

    Optional<TicketOrder> findTicketOrderByTicketsContains(Ticket ticket);

    /**
     * Find all tickets of a certain type from a certain user after a certain dateTime.
     *
     * @param userId the userId
     * @param type the orderType
     * @param dateTime the dateTime
     * @return all tickets with the same type that have their performance after a certain dateTime
     */
    @Query("""
        SELECT t FROM Ticket t
        INNER JOIN TicketOrder o ON t.orderId = o.id
        INNER JOIN Performance p ON t.performanceId = p.id
        WHERE o.userId = ?1 AND o.type = ?2 AND p.dateTime >?3""")
    List<Ticket> findTicketOrderByUserIdAndTypeAndDateTimeAfter(long userId, OrderType type, LocalDateTime dateTime);

    /**
     * Find all tickets of a certain type from a certain user before or during a certain dateTime.
     *
     * @param userId the userId
     * @param type the orderType
     * @param dateTime the dateTime
     * @return all tickets with the same type that have their performance before or during a certain dateTime
     */
    @Query("""
        SELECT t FROM Ticket t
        INNER JOIN TicketOrder o ON t.orderId = o.id
        INNER JOIN Performance p ON t.performanceId = p.id
        WHERE o.userId = ?1 AND o.type = ?2 AND p.dateTime <=?3""")
    Page<Ticket> findTicketOrderByUserIdAndTypeAndDateTimeLessThanEqual(long userId, OrderType type, LocalDateTime dateTime, Pageable pageable);

    /**
     * Count total tickets from a certain user with a certain type before or during a certain dateTime.
     *
     * @param userId the userId
     * @param orderType the orderType
     * @param dateTime the dateTime
     * @return total count
     */
    @Query("""
        SELECT count(t) FROM Ticket t
        INNER JOIN TicketOrder o ON t.orderId = o.id
        INNER JOIN Performance p ON t.performanceId = p.id
        WHERE o.userId = ?1 AND o.type = ?2 AND p.dateTime <=?3""")
    long getCountTicketsByUserIdAndOrderTypeAndDateTimeLessThanEqual(long userId, OrderType orderType, LocalDateTime dateTime);
}