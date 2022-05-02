package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceStatus;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import org.apache.commons.lang3.NotImplementedException;

import java.io.File;

public class Invoice {

    private long id;

    private InvoiceStatus status;
    private InvoiceType type;
    private InvoiceId identification;

    private String path;
    private File pdf;

    /**
     * retrieve the pdf of the invoice if it exists
     *
     * @return the pdf and loads it if it hasn't been loaded yet, if no pdf exists null is returned
     */
    public File getPdf() {
        throw new NotImplementedException();
    }

    /**
     * Loads the pdf from the specified path
     *
     * @param path the path of the file
     */
    private void loadFile( String path ) {
        throw new NotImplementedException();
    }
}
