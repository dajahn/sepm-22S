package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceStatus;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceProcessingService;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.util.Formatter;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceProcessingServiceImpl implements InvoiceProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PdfGenerationService pdfGenerationService;
    private final EmailService emailService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceProcessingServiceImpl(PdfGenerationService pdfGenerationService, EmailService emailService, InvoiceRepository invoiceRepository) {
        this.pdfGenerationService = pdfGenerationService;
        this.emailService = emailService;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Async("invoiceProcessingPoolTaskExecutor")
    public void process(Invoice invoice) {
        LOGGER.trace("process(Invoice invoice) with invoice={}", invoice);
        this.generatePdf(invoice);
        this.sendNotification(invoice);
    }

    @Override
    public void generatePdf(Invoice invoice) {
        LOGGER.trace("generatePdf(Invoice invoice) with invoice={}", invoice);
        Map<String, Object> data = new HashMap<>();
        data.put("invoice.number", invoice.getIdentification().toString());
        data.put("invoice.date", Formatter.formatDate(invoice.getDate()));

        // todo make values dynamic

        List<Object> tickets = new ArrayList<>();

        float totalPrice = 0;

        Order order = invoice.getOrder();

        for (Ticket ticket : order.getTickets()) { // todo add grouping for seats with same price and sector
            float price = (float) ((double) ticket.getSector().getPrice());
            int ticketAmount = 1;

            totalPrice += price * ticketAmount;

            tickets.add(new Object() {
                final String sector = ticket instanceof SeatTicket ? Formatter.formatSeatType(((SeatTicket) ticket).getSeatType()) : "Standing";
                final int amount = ticketAmount;
                final String singlePrice = Formatter.formatPrice(price);
                final String totalPrice = Formatter.formatPrice(price * ticketAmount);
            });
        }

        data.put("ticket", tickets);

        data.put("totalPrice", Formatter.formatPrice(totalPrice));
        data.put("totalTaxes", Formatter.formatPrice(totalPrice * 0.2f));

        Performance performance = order.getTickets().get(0).getPerformance(); // todo change if we want to support multiple performances

        data.put("event.title", performance.getEvent().getName());
        data.put("event.date", Formatter.formatDateTime(performance.getDateTime())); // todo add duration when it is added to the data structure
        data.put("event.location", performance.getLocation().getName());

        HtmlTemplate template = invoice.getType() == InvoiceType.CANCELLATION ? HtmlTemplate.PDF_CANCELLATION_INVOICE : HtmlTemplate.PDF_INVOICE;

        File pdf = pdfGenerationService.generate(template, data);

        invoice.setStatus(InvoiceStatus.GENERATED);
        invoiceRepository.save(invoice);

        invoice.setPdf(pdf);
    }

    @Override
    public void sendNotification(Invoice invoice) {
        LOGGER.trace("sendNotification(Invoice invoice) with invoice={}", invoice);
        if (invoice.getType() == InvoiceType.CANCELLATION) {
            emailService.sendCancellationInvoiceNotification(invoice);
        } else {
            emailService.sendInvoiceNotification(invoice);
        }
        invoice.setStatus(InvoiceStatus.DISTRIBUTED);
        invoiceRepository.save(invoice);
    }
}
