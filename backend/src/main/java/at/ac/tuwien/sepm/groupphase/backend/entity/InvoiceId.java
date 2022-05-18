package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvoiceId {

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
            // todo handle error
        }
        return identification;
    }
}
