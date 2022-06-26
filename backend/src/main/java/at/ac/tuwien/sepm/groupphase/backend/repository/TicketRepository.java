package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select t from Ticket t where t.cancellation is null and t.order.validUntil > current_date and t.performance.event.id = ?1 and t.performanceId = ?2")
    List<Ticket> findByEventIdAndPerformanceId(Long eventId, Long performanceId);

    List<Ticket> findByOrderTypeAndOrderUserIdAndIdIn(OrderType type, long userId, List<Long> ticketId);

    /**
     * Find all tickets of a certain type from a certain user after a certain dateTime.
     *
     * @param userId   the userId
     * @param type     the orderType
     * @param dateTime the dateTime
     * @return all tickets with the same type that have their performance after a certain dateTime
     */
    @Query("""
        select t from Ticket t
        where t.cancellation is null
        and t.order.userId = ?1 and t.order.type = ?2 and t.performance.dateTime > ?3
        order by t.performance.dateTime""")
    List<Ticket> findByOrderUserIdAndOrderTypeAndPerformanceDateTimeAfterOrderByPerformanceDateTime(long userId, OrderType type, LocalDateTime dateTime);


    /**
     * Find all tickets of a certain type from a certain user before or during a certain dateTime.
     *
     * @param userId   the userId
     * @param type     the orderType
     * @param dateTime the dateTime
     * @return all tickets with the same type that have their performance before or during a certain dateTime
     */
    @Query("""
        select t from Ticket t
        where t.cancellation is null
        and t.order.userId = ?1 and t.order.type = ?2 and t.performance.dateTime <= ?3
        order by t.performance.dateTime""")
    Page<Ticket> findByOrderUserIdAndOrderTypeAndPerformanceDateTimeLessThanEqualOrderByPerformanceDateTime(long userId, OrderType type, LocalDateTime dateTime, Pageable pageable);

    /**
     * Count total tickets from a certain user with a certain type before or during a certain dateTime.
     *
     * @param userId    the userId
     * @param orderType the orderType
     * @param dateTime  the dateTime
     * @return total count
     */
    @Query("""
        select count(t) from Ticket t
        where t.cancellation is null
        and t.order.userId = ?1 and t.order.type = ?2 and t.performance.dateTime <= ?3""")
    long countByOrderUserIdAndOrderTypeAndPerformanceDateTimeLessThanEqual(long userId, OrderType orderType, LocalDateTime dateTime);
}
