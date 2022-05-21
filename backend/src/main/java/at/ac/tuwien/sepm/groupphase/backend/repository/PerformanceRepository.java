package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("select p from Performance p join fetch p.location l join fetch l.sectors where p.event.id = ?1 and p.id = ?2")
    Optional<Performance> findByEventIdAndId(long eventId, long id);

    @Query(value = "select distinct p from Event e join e.performances p join p.location l join l.sectors s "
        + "where (p.event.id = e.id and UPPER(e.name) like UPPER(:eventName) or :eventName is null) "
        + "AND (p.dateTime <= :toDate or :toDate is null) "
        + "AND (p.id = l.id AND UPPER(l.name) like UPPER(:locationName) or :locationName is null) "
        + "AND (p.dateTime >= :fromDate or :fromDate is null) "
        + "AND (l.id = s.location.id AND s.price <= :toPrice AND s.price >= :fromPrice or :fromPrice = 0)")
    List<Performance> findAllBy(@Param("fromPrice") double fromPrice, @Param("toPrice") double toPrice,
                                @Param("eventName") String eventName, @Param("locationName") String locationName,
                                @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

}