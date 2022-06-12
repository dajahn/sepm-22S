package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("select t from Ticket t where t.cancellation is null and t.order.validUntil > current_date and t.performance.event.id = ?1 and t.performanceId = ?2")
    List<Ticket> findByEventIdAndPerformanceId(Long eventId, Long performanceId);

    Optional<Ticket> findByOrderTypeAndOrderUserIdAndId(OrderType type, long userId, long ticketId);
}
