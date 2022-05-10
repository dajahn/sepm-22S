package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;

@Data
public abstract class SectorDto {

    private Long id;
    private double price;
    private final SectorType type;

    public SectorDto() {
        this(null);
    }

    public SectorDto(SectorType type) {
        this.type = type;
    }
}

