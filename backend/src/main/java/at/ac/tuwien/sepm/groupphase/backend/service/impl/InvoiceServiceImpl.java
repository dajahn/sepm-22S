package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceId;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Override
    public void create(Invoice invoice) {
        throw new NotImplementedException();
    }

    @Override
    public void save(Invoice invoice) {
        throw new NotImplementedException();
    }

    @Override
    public void generate(Invoice invoice) {
        throw new NotImplementedException();
    }

    @Override
    public void send(Invoice invoice) {
        throw new NotImplementedException();
    }

    @Override
    public InvoiceId getNextInvoiceId() {
        return null;
    }
}
