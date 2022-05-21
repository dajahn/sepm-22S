package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.CheckoutTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CheckoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CheckoutEndpointTest implements CheckoutTestData {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setCheckoutDto(){
        CheckoutDto c = new CheckoutDto();
        c.setCardholder(CARDHOLDER);
        c.setCardnumber(CARDNUMBER);
        c.setExp(EXP);
        c.setCsc(CSC);
    }

    @Test
    public void givenNothing_whenCheckout_thenNoContentAndCartEmptyInDb() throws Exception {

    }
}
