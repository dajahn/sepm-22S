package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StatsDto;
import at.ac.tuwien.sepm.groupphase.backend.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/stats")
public class StatsEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final StatsService statsService;

    public StatsEndpoint(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    @PermitAll
    @Operation(summary = "Provides some stats about news, events & tickets")
    public StatsDto getStats() {
        LOGGER.info("GET /api/v1/stats");
        return statsService.getStats();
    }
}
