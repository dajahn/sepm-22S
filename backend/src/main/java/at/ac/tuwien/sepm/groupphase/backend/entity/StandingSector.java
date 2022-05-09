package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.embeddable.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.Objects;

/**
 * This entity represents a standing sector at a location.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StandingSector extends Sector {

    @Column(nullable = false)
    private Integer capacity;

    @Embedded
    private Point point1;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "x2")),
        @AttributeOverride(name = "y", column = @Column(name = "y2"))
    })
    private Point point2;

    @Override
    public SectorDto mapToDto() {
        return SectorMapper.INSTANCE.standingSectorToStandingSectorDto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        StandingSector that = (StandingSector) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
