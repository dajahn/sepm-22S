package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.SeatTicket;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepm.groupphase.backend.service.PdfGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.service.QRCodeGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import at.ac.tuwien.sepm.groupphase.backend.util.Formatter;
import at.ac.tuwien.sepm.groupphase.backend.util.HtmlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class TicketPrintingServiceImpl implements TicketPrintingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final QRCodeGenerationService qrCodeGenerationService;
    private final PdfGenerationService pdfGenerationService;
    private final EmailService emailService;
    private final TicketRepository ticketRepository;

    public TicketPrintingServiceImpl(QRCodeGenerationService qrCodeGenerationService, PdfGenerationService pdfGenerationService, EmailService emailService, TicketRepository ticketRepository) {
        this.qrCodeGenerationService = qrCodeGenerationService;
        this.pdfGenerationService = pdfGenerationService;
        this.emailService = emailService;
        this.ticketRepository = ticketRepository;
    }

    @Override
    @Async("ticketProcessingPoolTaskExecutor")
    public void processOrder(TicketOrder order) {
        for (Ticket ticket : order.getTickets()) {
            ticket.setPdf(generateTicket(ticket));
            ticketRepository.save(ticket);
        }

        sendNotification(order);
    }

    @Override
    public File generateTicket(Ticket ticket) {
        byte[] qrCodeBytes = qrCodeGenerationService.generate(ticket.getHash().toString());
        String qrCode = Base64.getEncoder().encodeToString(qrCodeBytes);

        Performance performance = ticket.getPerformance();

        boolean isSeatSector = ticket instanceof SeatTicket;

        Map<String, Object> data = new HashMap<>();
        data.put("qrcode", "data:image/jpeg;base64," + qrCode);
        data.put("title", performance.getEvent().getName());
        data.put("location", performance.getLocation().getName());
        data.put("date", Formatter.formatDateTime(performance.getDateTime()));
        data.put("isSeatSector", isSeatSector);

        if (isSeatSector) {
            SeatTicket seatTicket = ((SeatTicket) ticket);
            data.put("sector", Formatter.formatSeatType(seatTicket.getSector().getSeatType()));
            data.put("row", seatTicket.getSeat().getRow());
            data.put("seat", seatTicket.getSeat().getColumn());
        } else {
            data.put("sector", "Standing");
        }


        return pdfGenerationService.generate(HtmlTemplate.PDF_TICKET, data);
    }

    @Override
    public void sendNotification(TicketOrder order) {
        emailService.sendTicketNotification(order);
    }
}

