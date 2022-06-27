package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CancellationDto {

    @NotNull
    private List<Long> cancelTickets;
}
