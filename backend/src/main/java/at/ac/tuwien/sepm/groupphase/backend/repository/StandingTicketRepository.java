package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StandingTicketRepository extends JpaRepository<StandingTicket, Long> {

    @Query("select count(s) from StandingTicket s where s.cancellation is null and s.performance.id = ?1 and s.sector.id = ?2 and (s.order.validUntil is null or s.order.validUntil > current_date)")
    int countByPerformanceIdAndStandingSectorId(Long performanceId, Long standingSectorId);
}
