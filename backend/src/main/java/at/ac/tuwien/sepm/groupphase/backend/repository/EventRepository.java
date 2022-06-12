package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        "SELECT e FROM Event e JOIN e.performances p, TicketOrder o JOIN o.tickets t "
            + "WHERE (e.category) = :category "
            + "AND o.type = at.ac.tuwien.sepm.groupphase.backend.enums.OrderType.PURCHASE "
            + "AND t.performance.id = any(select ev.id from e.performances ev where ev.dateTime >= :fromDate and ev.dateTime <= :toDate) "
            + "AND p.dateTime >= :fromDate "
            + "AND p.dateTime <= :toDate "
            + "GROUP BY e.id ORDER BY count(distinct t.id) desc")
    List<Event> findTopTenByCategory(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") EventCategory category, Pageable pageable);

    /**
     * Find how many tickets were sold from each top ten event.
     *
     * @param fromDate start of month in which the top ten events are8
     * @param toDate   end of month in which the top ten events are
     * @param category category in which the events are
     * @return Count of sold tickets
     */
    @Query(value =
        "SELECT count(distinct t.id) FROM Event e JOIN e.performances p, TicketOrder o JOIN o.tickets t "
            + "WHERE (e.category) = :category "
            + "AND o.type = at.ac.tuwien.sepm.groupphase.backend.enums.OrderType.PURCHASE "
            + "AND t.performance.id = any(select ev.id from e.performances ev where ev.dateTime >= :fromDate and ev.dateTime <= :toDate) "
            + "AND p.dateTime >= :fromDate "
            + "AND p.dateTime <= :toDate "
            + "GROUP BY e.id ORDER BY count(distinct t.id) desc")
    List<Integer> topTenEventsTicketCount(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, @Param("category") EventCategory category, Pageable pageable);

    @Query(value = "select * from Event e "
        + "where (e.category = :category or :category = -1) "
        + "and (UPPER(e.description) like UPPER(:description) or :description is null) "
        + "and (e.duration >= :minDuration and e.duration <= :maxDuration or :minDuration is null) "
        + "and (UPPER(e.name) like UPPER(:name) or :name is null)", nativeQuery = true)
    List<Event> findAllBy(@Param("category") int category, @Param("description") String description, @Param("minDuration") Duration minDuration, @Param("maxDuration") Duration maxDuration, @Param("name") String name);
}