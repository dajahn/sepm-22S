package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceId;

public interface InvoiceService {

    /**
     * Creates a new invoice as well as setting a unique id.
     *
     * @param invoice the invoice which should be created
     */
    void create(Invoice invoice);

    /**
     * Saves the invoice details.
     *
     * @param invoice the invoice which should be saved
     */
    void save(Invoice invoice);

    /**
     * Generates a file representation of the invoice.
     *
     * @param invoice the invoice which should be generated
     */
    void generate(Invoice invoice);

    /**
     * Send the given invoice.
     *
     * @param invoice the invoice which should be sent
     */
    void send(Invoice invoice);

    /**
     * Identifies the next free invoice id of the current year,
     * additionally it starts with the first id if there is not yet an invoice for the current year available.
     *
     * @return the next free invoice id of the current year
     */
    InvoiceId getNextInvoiceId();
}
