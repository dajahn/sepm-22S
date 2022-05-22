package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import org.mapstruct.Mapper;

@Mapper
public interface FileMapper {

    default FileDto fileToFileDto(File file) {
        if (file == null) {
            return null;
        }

        FileDto dto = new FileDto();
        dto.setUrl("/files/" + file.getId().toString());
        return dto;
    }
}

