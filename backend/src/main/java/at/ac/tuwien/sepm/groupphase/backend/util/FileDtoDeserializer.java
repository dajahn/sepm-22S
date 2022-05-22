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
import java.util.Objects;

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

        if (node != null) {
            JsonNode id = node.get("id");
            if (id != null) {
                fileDto.setId(id.asLong());
            }

            JsonNode imageBase64 = node.get("imageBase64");
            if (imageBase64 != null) {
                fileDto.setImageBase64(imageBase64.asText());
            }

            JsonNode url = node.get("url");
            if (url != null) {
                fileDto.setUrl(url.asText());
            }

            JsonNode typeNode = node.get("type");
            if (typeNode != null) {
                String type = "";
                String subtype = "";
                if (typeNode.get("type") != null) {
                    type = typeNode.get("type").asText();
                }
                if (typeNode.get("subtype") != null) {
                    subtype = typeNode.get("subtype").asText();
                }
                if (Objects.equals(type, "")) {
                    fileDto.setType(new MediaType("image", "jpg"));
                } else {
                    fileDto.setType(new MediaType(type, subtype));
                }
            }
        }
        return fileDto;
    }
}
