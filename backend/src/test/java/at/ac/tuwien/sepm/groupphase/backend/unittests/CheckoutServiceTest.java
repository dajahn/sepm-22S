package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.CheckoutTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CheckoutDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class CheckoutServiceTest implements CheckoutTestData {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderRepository orderRepository;

    private CheckoutDto checkoutDto;

    @BeforeEach
    public void setCheckoutDto() {
        checkoutDto = new CheckoutDto();
        checkoutDto.setCardholder(CARDHOLDER);
        checkoutDto.setCardnumber(CARDNUMBER);
        checkoutDto.setExp(EXP);
        checkoutDto.setCsc(CSC);
    }

    @Test
    @Rollback
    @Transactional
    public void givenFilledCart_whenCheckout_thenCartEmpty() {
        // GIVEN
        List<TicketOrder> orders = orderRepository.findAll();
        TicketOrder cart = null;
        for (TicketOrder order : orders) {
            if (order.getType() == OrderType.CART && order.getTickets().size() > 0) {
                cart = order;
                break;
            }
        }
        cart.setValidUntil(LocalDateTime.now().plusMinutes(30));
        orderRepository.save(cart);
        boolean gt = false;
        if (cart.getTickets().size() > 0) {
            gt = true;
        }
        assertEquals(OrderType.CART, cart.getType());
        assertEquals(true, gt);

        // WHEN
        checkoutService.checkout(cart.getUserId(), checkoutDto);

        // THEN
        Optional<TicketOrder> tmp = orderRepository.findById(cart.getId());
        TicketOrder cart2 = tmp.get();
        assertEquals(OrderType.PURCHASE, cart2.getType());
    }

    @Test
    @Rollback
    @Transactional
    public void givenEmptyCart_whenCheckout_thenThrowValidationException() {
        // GIVEN
        Optional<TicketOrder> tmp = orderRepository.findById(1L);
        TicketOrder cart = tmp.get();
        cart.getTickets().clear();
        cart.setTickets(cart.getTickets());
        orderRepository.save(cart);
        tmp = orderRepository.findById(1L);
        cart = tmp.get();
        assertEquals(OrderType.CART, cart.getType());
        assertEquals(0, cart.getTickets().size());
        long userID = cart.getUserId();
        ValidationException vex;

        // WHEN
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));

        // THEN
        assertEquals("Cart tickets must not be empty!", vex.getMessage());
    }

    @Test
    @Rollback
    @Transactional
    public void givenValidCartInvalidCheckout_whenCheckout_thenThrowValidationException() {
        // GIVEN
        List<TicketOrder> orders = orderRepository.findAll();
        TicketOrder cart = null;
        for (TicketOrder order : orders) {
            if (order.getType() == OrderType.CART && order.getTickets().size() > 0) {
                cart = order;
                break;
            }
        }
        boolean gt = false;
        if (cart.getTickets().size() > 0) {
            gt = true;
        }
        assertEquals(OrderType.CART, cart.getType());
        assertEquals(true, gt);
        ValidationException vex;
        long userID = cart.getUserId();
        CheckoutDto checkoutDto = this.checkoutDto;

        // WHEN / THEN
        // - invalid name
        checkoutDto.setCardholder(null);
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Cardholder's name must not be null!", vex.getMessage());
        checkoutDto.setCardholder("");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Cardholder's name must not be empty!", vex.getMessage());
        checkoutDto.setCardholder("a".repeat(257));
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Cardholder's name is too long!", vex.getMessage());
        checkoutDto.setCardholder(CARDHOLDER);

        // - invalid card number
        checkoutDto.setCardnumber(null);
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Card number must not be null!", vex.getMessage());
        checkoutDto.setCardnumber("");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Card number must not be empty!", vex.getMessage());
        checkoutDto.setCardnumber("123456789abc");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Card number must be 16 numbers!", vex.getMessage());
        checkoutDto.setCardnumber(CARDNUMBER);

        // - invalid expiry date
        checkoutDto.setExp(null);
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Expiry date must not be null!", vex.getMessage());
        checkoutDto.setExp("");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Expiry date must not be empty!", vex.getMessage());
        checkoutDto.setExp("18/12");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Expiry date must follow the pattern MM/YY!", vex.getMessage());
        checkoutDto.setExp("12/12");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Credit card is expired!", vex.getMessage());
        checkoutDto.setExp("01/12");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("Credit card is expired!", vex.getMessage());
        checkoutDto.setExp(EXP);

        // - invalid cvv or cvc
        checkoutDto.setCsc(null);
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("CVV or CVC must not be null!", vex.getMessage());
        checkoutDto.setCsc("");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("CVV or CVC must not be empty!", vex.getMessage());
        checkoutDto.setCsc("12344");
        vex = assertThrows(ValidationException.class, () -> checkoutService.checkout(userID, checkoutDto));
        assertEquals("CVV or CVC must be 3-4 numbers!", vex.getMessage());
        checkoutDto.setCsc(CSC);
    }

}
