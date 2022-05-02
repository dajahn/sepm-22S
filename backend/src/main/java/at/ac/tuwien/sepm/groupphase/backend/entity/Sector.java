package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public abstract class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Location location;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Sector sector = (Sector) o;
        return id != null && Objects.equals(id, sector.id);
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
