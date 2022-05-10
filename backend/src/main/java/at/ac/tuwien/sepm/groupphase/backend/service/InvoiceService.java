package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;

public interface InvoiceService {

    /**
     * Creates a new invoice as well as setting a unique id.
     *
     * @param invoice the invoice which should be created
     */
    Invoice create(Invoice invoice);

    /**
     * Finds the next available invoice id and set it to the invoice.
     *
     * @param invoice the invoice which should be processed
     */
    void setNextFreeInvoiceId(Invoice invoice);

    /**
     * Saves the invoice details.
     *
     * @param invoice the invoice which should be saved
     */
    Invoice save(Invoice invoice);

}
