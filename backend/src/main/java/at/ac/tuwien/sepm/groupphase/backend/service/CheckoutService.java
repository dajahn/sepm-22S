package at.ac.tuwien.sepm.groupphase.backend.service;

public interface CheckoutService {

    /**
     * Updates the status of every CART order to PURCHASE order.
     *
     * @param userId the ID of the logged-in user
     */
    void checkout(Long userId);
}
