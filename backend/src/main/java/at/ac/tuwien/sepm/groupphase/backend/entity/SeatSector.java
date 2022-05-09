package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
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
@ToString
@RequiredArgsConstructor
public class SeatSector extends Seat {

    @OneToMany(cascade = {CascadeType.ALL})
    @OrderBy("row, column")
    @ToString.Exclude
    private List<Seat> seats = new ArrayList<>();

    @Column(nullable = false)
    private Type type;

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

    public enum Type {
        NONE,
        PR,
        VIP
    }
}
