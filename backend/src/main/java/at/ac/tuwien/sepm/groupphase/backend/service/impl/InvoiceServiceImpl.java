package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceId;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceProcessingService;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceProcessingService invoiceProcessingService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, InvoiceProcessingService invoiceProcessingService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceProcessingService = invoiceProcessingService;
    }

    @Override
    public Invoice create(Invoice invoice) {
        this.setNextFreeInvoiceId(invoice);
        invoiceProcessingService.process(invoice); // runs asynchronous
        return invoice;
    }

    @Override
    @Transactional
    public void setNextFreeInvoiceId(Invoice invoice) {
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
        invoiceRepository.save(invoice);
    }
}
