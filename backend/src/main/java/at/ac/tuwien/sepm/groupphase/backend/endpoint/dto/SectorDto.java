package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.enums.SectorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Data transfer object of the sector entity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StandingSectorDto.class, name = SectorType.Constants.STANDING_VALUE),
    @JsonSubTypes.Type(value = SeatSectorDto.class, name = SectorType.Constants.SEAT_VALUE)
})
@Data
public abstract class SectorDto {

    private Long id;
    private Float price;
    private final SectorType type;

    public SectorDto() {
        this(null);
    }

    public SectorDto(SectorType type) {
        this.type = type;
    }

    /**
     * Maps a StandingSectorDto or SeatSectorDto to its corresponding StandingSector or SeatSector.
     *
     * @param mapper the mapper used to Map sectorDto sub-classes to Sector sub-classes
     * @return the sector
     */
    public abstract Sector mapToEntity(SectorMapper mapper);
}
