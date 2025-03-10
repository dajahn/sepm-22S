package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object of the standing sector entity.
 */
@Data()
@EqualsAndHashCode(callSuper = true)
public class StandingSectorDto extends SectorDto {

    private String name;
    private Integer capacity;
    private PointDto point1;
    private PointDto point2;

    public StandingSectorDto() {
        super(SectorType.STANDING);
    }

    @Override
    public Sector mapToEntity(SectorMapper mapper) {
        return mapper.standingSectorDtoToStandingSector(this);
    }
}
