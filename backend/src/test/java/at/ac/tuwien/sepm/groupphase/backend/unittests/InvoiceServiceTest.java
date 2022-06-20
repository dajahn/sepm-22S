package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class InvoiceServiceTest {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @Rollback
    @Transactional
    public void givenInvoiceHasRequiredValues_whenCreateInvoice_thenInvoiceIsSaved() {
        // GIVEN
        TicketOrder order = orderRepository.findById(1L).orElseThrow();
        Invoice invoice = new Invoice(order, InvoiceType.NORMAL);

        // WHEN
        invoiceService.create(invoice);

        // THEN
        Invoice stored = invoiceRepository.findById(invoice.getId()).orElseThrow();

        assertEquals(
            stored,
            invoice
        );
    }


    @Test
    @Rollback
    @Transactional
    public void givenInvoiceHasRequiredValues_whenCreateInvoice_thenInvoiceGetsIdentification() {
        // GIVEN
        TicketOrder order = orderRepository.findById(1L).orElseThrow();
        Invoice invoice = new Invoice(order, InvoiceType.NORMAL);

        // WHEN
        invoiceService.create(invoice);

        // THEN
        assertNotNull(
            invoice.getIdentification()
        );
    }

    @Test
    @Rollback
    @Transactional
    public void givenOrderExists_whenCreateInvoiceFromOrder_thenInvoiceIsCreated() {
        // GIVEN
        TicketOrder order = orderRepository.findById(1L).orElseThrow();

        // WHEN
        Invoice result = invoiceService.create(order);

        // THEN
        // invoice not null
        assertNotNull(
            result
        );

        // order equals input
        assertEquals(
            result.getOrder(),
            order
        );
    }
}
