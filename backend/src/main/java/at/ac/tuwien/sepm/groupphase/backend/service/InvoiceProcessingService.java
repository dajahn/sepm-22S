package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;

public interface InvoiceProcessingService {

    /**
     * Processes am invoice asynchronously (generates pdf and sends email).
     *
     * @param invoice the invoice which should be processed
     */
    void process(Invoice invoice);

    /**
     * Generates a file representation of the invoice.
     *
     * @param invoice the invoice which should be generated
     */
    void generatePdf(Invoice invoice);

    /**
     * Send the given invoice.
     *
     * @param invoice the invoice which should be sent
     */
    void sendNotification(Invoice invoice);
}
