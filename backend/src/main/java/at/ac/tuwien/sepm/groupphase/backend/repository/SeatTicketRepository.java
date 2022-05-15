package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.StandingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTicketRepository extends JpaRepository<SeatTicket, Long> {

    @Query("select (count(s) > 0) from SeatTicket s where s.performance.id = ?1 and s.seat.id = ?2 and (s.order.validUntil is null or s.order.validUntil > current_date)")
    boolean existsByPerformanceIdAndSeatId(Long performanceId, Long seatId);
}
