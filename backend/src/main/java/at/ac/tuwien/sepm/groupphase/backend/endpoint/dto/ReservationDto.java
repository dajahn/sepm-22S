package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    @NotNull(message = "Order ID must be specified.")
    private Long orderId;
    @NotNull(message = "Ticket ID must be specified.")
    private Long ticketId;

    @NotNull(message = "Performance must be specified.")
    private Long performance;

    @NotNull(message = "Type must be specified and either 'STANDING' or 'SEAT'.")
    private SectorType type;

    @NotNull(message = "Item must be specified.")
    private Long item;
}
