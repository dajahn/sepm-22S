package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.entity.File;
import at.ac.tuwien.sepm.groupphase.backend.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/files")
public class FileEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final FileService fileService;

    public FileEndpoint(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "/{id}")
    @PermitAll
    @Transactional(readOnly = true)
    public ResponseEntity<?> findById(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/files/{}", id);
        File file = fileService.findById(id);
        return ResponseEntity.ok().contentType(file.getType()).body(file.getData());
    }

}
