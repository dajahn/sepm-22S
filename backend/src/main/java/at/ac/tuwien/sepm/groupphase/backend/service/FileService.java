package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;

import java.io.IOException;

public interface FileService {
    /**
     * Service to Store a file to the database.
     *
     * @param f file from User Input
     * @return Created File
     */
    File create(FileDto f) throws IOException;
}
