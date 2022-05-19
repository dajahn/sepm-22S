package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TicketPrintingServiceTest implements TestData {

    @Autowired
    private TicketPrintingService ticketPrintingService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void whenNewPdfTicketIsGenerated_thenQrCodeIsCreated() {
        Optional<TicketOrder> order = orderRepository.findById(1L);
        if (order.isEmpty()) {
            System.out.println("empty");
            return;
        }
        System.out.println(order.get());
        ticketPrintingService.processOrder(order.get());
    }
}
