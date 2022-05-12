package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Order;

public interface InvoiceService {

    /**
     * Creates a new invoice for the supplised order.
     *
     * @param order the order from which an invoice should be created
     */
    void create(Order order);

    /**
     * Creates a new invoice as well as setting a unique id.
     *
     * @param invoice the invoice which should be created
     */
    void create(Invoice invoice);

    /**
     * Finds the next available invoice id of the year and sets it to the invoice.
     *
     * @param invoice the invoice which should be processed
     */
    void setNextFreeInvoiceId(Invoice invoice);

    /**
     * Saves the invoice details.
     *
     * @param invoice the invoice which should be saved
     */
    void save(Invoice invoice);

}
