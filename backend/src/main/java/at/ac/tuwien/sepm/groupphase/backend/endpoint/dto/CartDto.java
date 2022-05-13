package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDto {

    private LocalDateTime dateTime;
    private List<TicketDto> tickets;
    private LocalDateTime validUntil;
}
