package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;

public interface InvoiceService {

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

    /**
     * Cancels the given invoice and created a new cancellation invoice.
     *
     * @param invoice the invoice which should be canceled
     * @return the newly created cancellation invoice
     * @throws at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException if the provided invoice does not match the required criteria
     */
    Invoice cancel(Invoice invoice);

}
