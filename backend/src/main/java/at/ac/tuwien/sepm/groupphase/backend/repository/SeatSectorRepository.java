package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatSectorRepository extends JpaRepository<SeatSector, Long> {

    @Query("select s from SeatSector s, Performance p where s.location.id = p.location.id and p.id = ?1")
    List<SeatSector> findByPerformanceId(Long performanceId);

}
