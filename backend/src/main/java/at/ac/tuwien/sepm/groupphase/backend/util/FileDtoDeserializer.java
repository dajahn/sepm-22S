package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.MediaType;

import java.io.IOException;

public class FileDtoDeserializer extends StdDeserializer<FileDto> {
    public FileDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    public FileDtoDeserializer() {
        this(null);
    }


    @Override
    public FileDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        FileDto fileDto = new FileDto();
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        JsonNode id = node.get("id");
        fileDto.setId(id.asLong());

        JsonNode imageBase64 = node.get("imageBase64");
        fileDto.setImageBase64(imageBase64.asText());

        JsonNode url = node.get("url");
        fileDto.setUrl(url.asText());

        JsonNode typeNode = node.get("type");
        String type = typeNode.get("type").asText();
        String subtype = typeNode.get("subtype").asText();
        fileDto.setType(new MediaType(type, subtype));
        return fileDto;
    }
}
