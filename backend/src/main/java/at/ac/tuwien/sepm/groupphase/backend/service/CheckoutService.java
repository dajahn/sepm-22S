package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CheckoutDto;

public interface CheckoutService {

    /**
     * Updates the status of every CART order to PURCHASE order.
     *
     * @param userId the ID of the logged-in user
     * @param checkoutDto to validate the payment data
     */
    void checkout(Long userId, CheckoutDto checkoutDto);
}
