package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.time.Duration;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Returns all Events matching the name substring.
     *
     * @param name substring of the searche event name
     * @return List of Events
     */
    List<Event> findByNameContaining(@Param("name") String name);

    List<Event> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("select e from Event e join fetch e.performances where e.id = ?1")
    Optional<Event> findById(Long id);

    /**
     * Find all top ten events by category.
     *
     * @param fromDate start of month in which the top ten events should be found
     * @param toDate   end of month in which the top ten events should be found
     * @param category category in which the events should be
     * @return Events
     */
    @Query(value =
        "SELECT e.* FROM ticket_order o JOIN Ticket t JOIN Performance p JOIN Event e "
            + "WHERE (e.category) = :category "
            + "AND o.type = 1 "
            + "AND o.id = t.order_id "
            + "AND p.id = t.performance_id "
            + "AND p.event_id = e.id "
            + "AND p.date_time >= :fromDate "
            + "AND p.date_time <= :toDate "
            + "GROUP BY e.id ORDER BY count(t.id) DESC limit 10", nativeQuery = true)
    List<Event> findTopTenByCategory(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") int category);

    /**
     * Find how many tickets were sold from each top ten event.
     *
     * @param fromDate start of month in which the top ten events are
     * @param toDate   end of month in which the top ten events are
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
            + "AND p.date_time >= :fromDate "
            + "AND p.date_time <= :toDate "
            + "GROUP BY e.id ORDER BY count(t.id) DESC limit 10", nativeQuery = true)
    List<Integer> topTenEventsTicketCount(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") int category);

    @Query(value = "select * from Event e "
            + "where (e.category = :category or :category = -1) "
            + "and (UPPER(e.description) like UPPER(:description) or :description is null) "
            + "and (e.duration = :duration or :duration is null) "
            + "and (UPPER(e.name) like UPPER(:name) or :name is null)", nativeQuery = true)
    List<Event> findAllBy(@Param("category") int category, @Param("description") String description, @Param("duration") Duration duration, @Param("name") String name);
}