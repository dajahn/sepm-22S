package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SmallSeatSectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.SectorMapper;
import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This entity represents a seat sector at a location, consisting of seats.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@RequiredArgsConstructor
public class SeatSector extends Sector {

    @Column(nullable = false)
    @NonNull
    private SeatType seatType;

    @OneToMany(fetch = javax.persistence.FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy("row, column")
    @NonNull
    private List<Seat> seats = new ArrayList<>();

    @Override
    public SeatSectorDto mapToDto(SectorMapper mapper) {
        return mapper.seatSectorToSeatSectorDto(this);
    }

    @Override
    public SmallSeatSectorDto mapToSmallDto(SectorMapper mapper) {
        return mapper.seatSectorToSmallSeatSectorDto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SeatSector that = (SeatSector) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
