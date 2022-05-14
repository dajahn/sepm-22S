package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FileDtoSerializer extends StdSerializer<FileDto> {
    protected FileDtoSerializer(Class<FileDto> t) {
        super(t);
    }

    public FileDtoSerializer() {
        this(null);
    }

    public void serialize(FileDto fileDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", (fileDto.getId() != null ? fileDto.getId().toString() : null));
        jsonGenerator.writeStringField("type", fileDto.getType().toString());
        jsonGenerator.writeStringField("imageBase64", fileDto.getImageBase64());
        jsonGenerator.writeStringField("url", fileDto.getUrl());
        jsonGenerator.writeEndObject();
    }
}
