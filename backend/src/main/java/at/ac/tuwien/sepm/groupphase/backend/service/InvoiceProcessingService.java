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
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotGeneratePdfException if there was an error generating the invoice pdf
     */
    void generatePdf(Invoice invoice);

    /**
     * Send the given invoice.
     *
     * @param invoice the invoice which should be sent
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.CouldNotDistributeException if there was an error sending the invoice notification email
     */
    void sendNotification(Invoice invoice);
}
