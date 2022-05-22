package at.ac.tuwien.sepm.groupphase.backend.entity.converter;

import org.springframework.http.MediaType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MediaTypeConverter implements AttributeConverter<MediaType, String> {

    @Override
    public String convertToDatabaseColumn(MediaType mediaType) {
        return mediaType.toString();
    }

    @Override
    public MediaType convertToEntityAttribute(String string) {
        return MediaType.parseMediaType(string);
    }
}
