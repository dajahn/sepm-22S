package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Find the Cart of a user by their ID.
     *
     * @param userId the ID of the user
     * @return the cart uf the user
     */
    Cart findCartByUserId(long userId);
}
