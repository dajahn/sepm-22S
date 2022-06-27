package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Cancellation;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.OrderInvoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.repository.CancellationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
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
    private TicketRepository ticketRepository;

    @Autowired
    private CancellationRepository cancellationRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    @Transactional
    @Rollback
    public void givenInvoiceHasRequiredValues_whenCreateInvoice_thenInvoiceIsSaved() {
        // GIVEN
        TicketOrder order = orderRepository.findById(1L).orElseThrow();
        Invoice invoice = new OrderInvoice(order);

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
    @Transactional
    @Rollback
    public void givenInvoiceHasRequiredValues_whenCreateInvoice_thenInvoiceGetsIdentification() {
        // GIVEN
        TicketOrder order = orderRepository.findById(1L).orElseThrow();
        Invoice invoice = new OrderInvoice(order);

        // WHEN
        invoiceService.create(invoice);

        // THEN
        assertNotNull(
            invoice.getIdentification()
        );
    }

    @Test
    @Transactional
    @Rollback
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
        assertThat(Arrays.equals(result.getTickets().toArray(), order.getTickets().toArray())).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    public void givenOrderExists_whenCreateInvoiceFromCancellation_thenInvoiceIsCreated() {
        // GIVEN
        Ticket ticket = ticketRepository.findById(1L).orElseThrow();
        Cancellation cancellation = ticket.getCancellation();
        if (cancellation == null) {
            cancellation = new Cancellation(LocalDateTime.now(), ticket.getOrder().getUserId(), Collections.singletonList(ticket));
            cancellation = cancellationRepository.save(cancellation);
        }

        // WHEN
        Invoice result = invoiceService.create(cancellation);

        // THEN
        // invoice not null
        assertNotNull(
            result
        );

        // order equals input
        assertThat(Arrays.equals(result.getTickets().toArray(), new Ticket[]{ticket})).isTrue();
    }
}
