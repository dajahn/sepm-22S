package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.FileRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import at.ac.tuwien.sepm.groupphase.backend.util.ImageUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public File create(FileDto f) throws IOException {
        LOGGER.trace("create() FileDto:{}", f);

        byte[] toStore = Base64.getDecoder().decode(f.getImageBase64());

        File file = File.builder()
            .type(f.getType())
            .data(toStore)
            .build();

        file = this.fileRepository.save(file);
        return file;
    }

    @Override
    public File findById(Long id) {
        LOGGER.debug("Find file with id {}", id);
        Optional<File> file = fileRepository.findById(id);
        if (file.isPresent()) {
            return file.get();
        } else {
            throw new NotFoundException(String.format("Could not find file with id %s", id));
        }
    }
}
