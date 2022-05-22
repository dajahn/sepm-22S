package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<TicketOrder, Long> {

    /**
     * Find one order of a user by their ID and the order type.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     * @return the order uf the user
     */
    Optional<TicketOrder> findFirstByTypeAndUserIdOrderByValidUntil(OrderType type, long userId);

    /**
     * Delete all orders of a certain type made by a user.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     */
    void deleteAllByTypeAndUserId(OrderType type, long userId);

    /**
     * Find all orders of a user by their ID and the order type.
     *
     * @param type   the type of the order
     * @param userId the ID of the user
     * @return all orders uf the user
     */
    List<TicketOrder> findTicketOrdersByTypeAndUserId(OrderType type, long userId);

    /**
     * Delete all orders made by a user.
     *
     * @param userId the ID of the user
     */
    void deleteAllByUserId(long userId);
}
