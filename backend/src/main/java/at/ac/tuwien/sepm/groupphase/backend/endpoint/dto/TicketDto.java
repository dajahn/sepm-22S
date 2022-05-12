package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public abstract class TicketDto {

    private Long id;
    private PerformanceDto performance;
}
