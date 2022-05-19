package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CheckoutValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * Validates a CheckoutDto.
     *
     * @param checkoutDto CheckoutDto to validate
     * @throws ValidationException if the DTO is not valid
     */
    public void validateCheckout(CheckoutDto checkoutDto) {
        LOGGER.trace("validateCheckout with {}", checkoutDto);

        if (checkoutDto == null) {
            throw new ValidationException("Checkout must not be null!");
        }

        validateCardholder(checkoutDto);
        validateCardnumber(checkoutDto);
        validateExp(checkoutDto);
        validateCsc(checkoutDto);
    }

    /**
     * Validates a Cart.
     *
     * @param cart Cart to validate
     * @throws ValidationException if the cart is empty
     */
    public void validateCart(TicketOrder cart) {
        if (cart.getTickets().isEmpty()) {
            throw new ValidationException("Cart tickets must not be empty!");
        }
    }

    private void validateCardholder(CheckoutDto checkoutDto) {
        //validate cardholder's name
        if (checkoutDto.getCardholder() == null) {
            throw new ValidationException("Cardholder's name must not be null!");
        }
        if (checkoutDto.getCardholder().trim().isEmpty()) {
            throw new ValidationException("Cardholder's name must not be empty!");
        }
        if (checkoutDto.getCardholder().length() > 256) {
            throw new ValidationException("Cardholder's name is too long!");
        }
    }

    private void validateCardnumber(CheckoutDto checkoutDto) {
        //validate cardnumber
        if (checkoutDto.getCardnumber() == null) {
            throw new ValidationException("Card number must not be null!");
        }
        if (checkoutDto.getCardnumber().trim().isEmpty()) {
            throw new ValidationException("Card number must not be empty!");
        }
        if (!checkoutDto.getCardnumber().matches("^[0-9]{16}$")) {
            throw new ValidationException("Card number must be 16 numbers!");
        }
    }

    private void validateExp(CheckoutDto checkoutDto) {
        //validate expiry date
        if (checkoutDto.getExp() == null) {
            throw new ValidationException("Expiry date must not be null!");
        }
        if (checkoutDto.getExp().trim().isEmpty()) {
            throw new ValidationException("Expiry date must not be empty!");
        }
        if (!checkoutDto.getExp().matches("^(0[1-9]|1[0-2])/?([0-9]{4}|[0-9]{2})$")) {
            throw new ValidationException("Expiry date must follow the pattern MM/YY!");
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yy"));
        if (Integer.parseInt(today.substring(3, 5)) > Integer.parseInt(checkoutDto.getExp().substring(3, 5))) {
            throw new ValidationException("Credit cart is expired!");
        } else if (Integer.parseInt(today.substring(3, 5)) == Integer.parseInt(checkoutDto.getExp().substring(3, 5))) {
            if (Integer.parseInt(today.substring(0, 2)) > Integer.parseInt(checkoutDto.getExp().substring(0, 2))) {
                throw new ValidationException("Credit cart is expired!");
            }
        }
    }

    private void validateCsc(CheckoutDto checkoutDto) {
        //validate cvv or cvc
        if (checkoutDto.getCsc() == null) {
            throw new ValidationException("CVV or CVC must not be null!");
        }
        if (checkoutDto.getCsc().trim().isEmpty()) {
            throw new ValidationException("CVV or CVC must not be empty!");
        }
        if (!checkoutDto.getCsc().matches("^[0-9]{3,4}$")) {
            throw new ValidationException("CVV or CVC must be 3-4 numbers!");
        }
    }

}
