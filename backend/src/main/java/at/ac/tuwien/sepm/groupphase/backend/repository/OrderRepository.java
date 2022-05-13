package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<TicketOrder, Long> {

    /**
     * Find one order of a user by their ID.
     *
     * @param userId the ID of the user
     * @return the order uf the user
     */
    Optional<TicketOrder> findByTypeAndUserId(OrderType type, long userId);
}
