package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
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

        List<Object> events = new ArrayList<>();

        float totalPrice = 0;
        int sign = invoice.getType() == InvoiceType.CANCELLATION ? -1 : 1;

        HashMap<Long, PerformanceItem> performances = new HashMap<>();

        for (Ticket ticket : invoice.getTickets()) {

            Performance performance = ticket.getPerformance();
            Long id = performance.getId();
            PerformanceItem item = performances.get(id);
            if (item == null) {
                item = new PerformanceItem(
                    performance.getEvent().getName(),
                    Formatter.formatDateTime(performance.getDateTime()),
                    performance.getLocation().getName()
                );
                performances.put(id, item);
            }

            String hash = ticket.getClass() + ":" + ticket.getSector().getId();
            if (item.tickets.get(hash) != null) {
                item.tickets.get(hash).increment();
            } else {
                item.tickets.put(hash, new TicketItem(ticket, sign));
            }
        }

        for (Long id : performances.keySet()) {
            PerformanceItem performance = performances.get(id);
            List<Object> ticketsItems = new ArrayList<>();

            for (String hash : performance.tickets.keySet()) {
                TicketItem ticket = performance.tickets.get(hash);

                float linePrice = ticket.price * ticket.amount;
                totalPrice += linePrice;

                ticketsItems.add(new Object() {
                    final String sector = ticket.sector;
                    final int amount = ticket.amount;
                    final String singlePrice = Formatter.formatPrice(ticket.price);
                    final String totalPrice = Formatter.formatPrice(linePrice);
                });

            }

            events.add(new Object() {
                final String title = performance.name;
                final String date = performance.date;
                final String location = performance.location;
                final List<Object> tickets = ticketsItems;
            });
        }

        data.put("events", events);
        data.put("totalPrice", Formatter.formatPrice(totalPrice));
        data.put("totalTaxes", Formatter.formatPrice(totalPrice * 0.2f));

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

@SuppressWarnings("checkstyle:OneTopLevelClass")
class TicketItem {
    String sector;
    int amount = 1;
    float price;

    public TicketItem(Ticket ticket, int sign) {
        this.sector = ticket instanceof SeatTicket ? Formatter.formatSeatType(((SeatTicket) ticket).getSector().getSeatType()) : "Standing";
        this.price = sign * Float.parseFloat(ticket.getSector().getPrice().toString());
    }

    public void increment() {
        this.amount++;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
class PerformanceItem {
    String name;
    String date;
    String location;
    HashMap<String, TicketItem> tickets = new HashMap<>();

    public PerformanceItem(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }
}

