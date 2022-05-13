package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("select p from Performance p join fetch p.location l join fetch l.sectors where p.event.id = ?1 and p.id = ?2")
    Optional<Performance> findByEventIdAndId(long eventId, long id);
}
