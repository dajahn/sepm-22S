package at.ac.tuwien.sepm.groupphase.backend.entity.converter;

import at.ac.tuwien.sepm.groupphase.backend.entity.InvoiceId;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class InvoiceIdConverter implements AttributeConverter<InvoiceId, String> {

    @Override
    public String convertToDatabaseColumn(InvoiceId id) {
        return id.toString();
    }

    @Override
    public InvoiceId convertToEntityAttribute(String string) {
        return InvoiceId.parseInvoiceId(string);
    }
}
