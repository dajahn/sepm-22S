package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.StandingSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandingSectorRepository extends JpaRepository<StandingSector, Long> {

    @Query("select s from StandingSector s, Performance p where s.location.id = p.location.id and p.id = ?1")
    List<StandingSector> findByPerformanceId(Long performanceId);

}
