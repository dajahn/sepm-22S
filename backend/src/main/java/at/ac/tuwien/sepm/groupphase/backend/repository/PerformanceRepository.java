package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Address;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("select p from Performance p join fetch p.location l join fetch l.sectors where p.event.id = ?1 and p.id = ?2")
    Optional<Performance> findByEventIdAndId(long eventId, long id);

    @Query(value = "select distinct p.* from Performance p join Location l join Event e "
        + "where (p.date_time >= :fromDate or :fromDate is null)"
        + "AND (p.date_time <= :toDate or :toDate is null) "
        + "AND (p.location_id = l.id AND UPPER(l.name) like UPPER(:locationName) or :locationName is null) "
        + "AND (p.event_id = e.id and UPPER(e.name) like UPPER(:eventName) or :eventName = null)", nativeQuery = true)
    List<Performance> findAllBy(@Param("eventName") String eventName, @Param("locationName") String locationName, @Param("fromDate") LocalDateTime fromDate, @Param("toDate")LocalDateTime toDate);
}