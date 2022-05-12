package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateTicketDto {

    @NotNull(message = "Performance must be specified.")
    private Long performance;

    @NotNull(message = "Type must be specified and either 'STANDING' or 'SEAT'.")
    private SectorType type;

    @NotNull(message = "Item must be specified.")
    private Long item;
}
