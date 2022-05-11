package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;

public interface FileService {

    /**
     * Find a single file by id.
     *
     * @param id the id of the file
     * @return the file
     */
    File findById(Long id);
}
