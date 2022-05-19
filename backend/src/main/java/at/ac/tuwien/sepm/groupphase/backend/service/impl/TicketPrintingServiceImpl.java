package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import at.ac.tuwien.sepm.groupphase.backend.service.QRCodeGenerationService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketPrintingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
public class TicketPrintingServiceImpl implements TicketPrintingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final QRCodeGenerationService qrCodeGenerationService;

    public TicketPrintingServiceImpl(QRCodeGenerationService qrCodeGenerationService) {
        this.qrCodeGenerationService = qrCodeGenerationService;
    }

    @Override
    public void processOrder(TicketOrder order) {

    }

    @Override
    public byte[] generateTicket(Ticket ticket) {
        byte[] qrCode = qrCodeGenerationService.generate("test string");
        return new byte[0];
    }

    @Override
    public void sendNotification(TicketOrder order, List<byte[]> tickets) {

    }
}

