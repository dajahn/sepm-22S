package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data()
@EqualsAndHashCode(callSuper = true)
public class StandingSectorDto extends SectorDto {

    private Integer capacity;
    private PointDto point1;
    private PointDto point2;

    public StandingSectorDto() {
        super(SectorType.STANDING_SECTOR);
    }
}

