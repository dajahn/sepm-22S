package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * This entity represents a sector at a location.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = javax.persistence.FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @NonNull
    private Location location;

    @Column(nullable = false)
    @NonNull
    private Double price;

    /**
     * Maps a StandingSector or SeatSector to its corresponding StandingSectorDto or SeatSectorDto.
     *
     * @param mapper the mapper used to Map sector sub-classes to SectorDto sub-classes
     * @return the sector DTO
     */
    public abstract SectorDto mapToDto(SectorMapper mapper);

    /**
     * Maps a StandingSector or SeatSector to its corresponding StandingSectorDto or SmallSeatSectorDto.
     *
     * @param mapper the mapper used to Map sector sub-classes to small SectorDto sub-classes
     * @return the small sector DTO
     */
    public abstract SectorDto mapToSmallDto(SectorMapper mapper);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Sector sector = (Sector) o;
        return id != null && Objects.equals(id, sector.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
