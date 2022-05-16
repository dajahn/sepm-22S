package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find all top ten events by category.
     *
     * @param fromDate start of month in which the top ten events should be found
     * @param toDate end of month in which the top ten events should be found
     * @param category category in which the events should be
     * @return Events
     */
    @Query(value =
        "SELECT e.id, e.name, e.description, e.duration, e.thumbnail_id, e.category FROM ticket_order o JOIN Ticket t JOIN Performance p JOIN Event e "
            + "WHERE (e.category) = :category "
            + "AND o.type = 1 "
            + "AND o.id = t.order_id "
            + "AND p.id = t.performance_id "
            + "AND p.event_id = e.id "
            + "AND (e.category) = :category "
            + "AND p.date_time >= :fromDate "
            + "AND p.date_time <= :toDate "
            + "GROUP BY e.id ORDER BY count(t.id) DESC limit 10", nativeQuery = true)
    List<Event> findAllByCategory(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") int category);

    /**
     * Find how many tickets were sold from each top ten event.
     *
     * @param fromDate start of month in which the top ten events are
     * @param toDate end of month in which the top ten events are
     * @param category category in which the events are
     * @return Count of sold tickets
     */
    @Query(value =
        "select count(t.id) FROM ticket_order o JOIN Ticket t JOIN Performance p JOIN Event e "
            + "WHERE (e.category) = :category "
            + "AND o.type = 1 "
            + "AND o.id = t.order_id "
            + "AND p.id = t.performance_id "
            + "AND p.event_id = e.id "
            + "AND (e.category) = :category "
            + "AND p.date_time >= :fromDate "
            + "AND p.date_time <= :toDate "
            + "GROUP BY e.id ORDER BY count(t.id) DESC limit 10", nativeQuery = true)
    List<Integer> topTenEventsTicketCount(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") int category);

}