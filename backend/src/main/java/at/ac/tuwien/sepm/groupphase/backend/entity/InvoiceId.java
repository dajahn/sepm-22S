package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.exception.UnexpectedException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Getter
@RequiredArgsConstructor
public class InvoiceId {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @NonNull
    private Integer year;

    @NonNull
    private Integer id;

    private InvoiceId() {
    }

    @Override
    public String toString() {
        return year + "-" + id;
    }

    public static InvoiceId parseInvoiceId(String s) {
        String[] parts = s.split("-");
        if (parts.length != 2) {
            return null;
        }
        InvoiceId identification = new InvoiceId();
        try {
            identification.year = Integer.parseInt(parts[0]);
            identification.id = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new UnexpectedException();
        }
        return identification;
    }
}
