package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.File;

import java.io.IOException;

public interface FileService {

    /**
     * Find a single file by id.
     *
     * @param id the id of the file
     * @return the file
     */
    File findById(Long id);

    //    /**
    //     * Service to Store a file to the database.
    //     *
    //     * @param f file from User Input
    //     * @return Created File
    //     * @throws IOException when file cannot be loaded
    //     */
    //    File create(FileDto f) throws IOException;

}
