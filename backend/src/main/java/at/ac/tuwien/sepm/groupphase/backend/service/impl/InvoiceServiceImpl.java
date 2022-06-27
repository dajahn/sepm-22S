package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Cancellation;
import at.ac.tuwien.sepm.groupphase.backend.entity.CancellationInvoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceId;
import at.ac.tuwien.sepm.groupphase.backend.entity.OrderInvoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceProcessingService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProcessingService invoiceProcessingService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProcessingService invoiceProcessingService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProcessingService = invoiceProcessingService;
    }

    @Override
    public OrderInvoice create(TicketOrder order) {
        LOGGER.trace("create(Order order) with order={}", order);

        OrderInvoice invoice = new OrderInvoice(order);
        this.create(invoice);
        return invoice;
    }

    @Override
    public CancellationInvoice create(Cancellation cancellation) {
        LOGGER.trace("create(Cancellation cancellation) with cancellation={}", cancellation);

        CancellationInvoice invoice = new CancellationInvoice(cancellation);
        this.create(invoice);
        return invoice;
    }

    @Override
    public void create(Invoice invoice) {
        LOGGER.trace("create(Invoice invoice) with invoice={}", invoice);
        this.setNextFreeInvoiceId(invoice);
        invoiceProcessingService.process(invoice); // runs asynchronous
    }

    @Override
    @Transactional
    public void setNextFreeInvoiceId(Invoice invoice) {
        LOGGER.trace("setNextFreeInvoiceId(Invoice invoice) with invoice={}", invoice);
        Invoice last = invoiceRepository.findFirstByOrderByIdDesc();
        InvoiceId lastId = last != null ? last.getIdentification() : null;

        LocalDateTime today = LocalDateTime.now();
        boolean isFirstOfYear = lastId == null || lastId.getYear() != today.getYear();
        InvoiceId newId = new InvoiceId(today.getYear(), isFirstOfYear ? 1 : lastId.getId() + 1);
        invoice.setIdentification(newId);

        this.save(invoice);
    }

    @Override
    public void save(Invoice invoice) {
        LOGGER.trace("save(Invoice invoice) with invoice={}", invoice);
        invoiceRepository.save(invoice);
    }

    //TODO: consult raphael about removal of these methods
    /*
    @Override
    @Transactional
    public Invoice cancel(Invoice invoice) {
        LOGGER.trace("cancel(Invoice invoice) with invoice={}", invoice);

        if (invoice.getType() != InvoiceType.NORMAL) {
            throw new ValidationException("The invoice which should be canceled must be from type 'NORMAL'.");
        }

        // create cancellation invoice
        Invoice cancellation = Invoice.builder()
            .reference(invoice)
            .type(InvoiceType.CANCELLATION)
            .status(InvoiceStatus.CREATED)
            .tickets(invoice.getTickets())
            .date(LocalDate.now())
            .build();

        this.create(cancellation);

        // update existing invoice
        invoice.setType(InvoiceType.CANCELED);
        invoice.setReference(cancellation);
        this.save(invoice);

        return cancellation;
    }

    @Transactional
    @Override
    public Invoice cancel(TicketOrder order) {
        List<Invoice> invoices = invoiceRepository.findAllByOrderIdAndType(order.getId(), InvoiceType.NORMAL);

        if (invoices == null || invoices.size() == 0) {
            throw new ValidationException("There is no invoice with type 'NORMAL' for the given order.");
        }

        if (invoices.size() > 1) {
            throw new ValidationException("There are to many invoice with type 'NORMAL' for the given order. Specify the invoice directly");
        }

        return this.cancel(invoices.get(0));
    }
    */
}
