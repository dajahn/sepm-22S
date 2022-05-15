package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This entity represents the performance of an event at a certain location, date and time.
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"date_time", "location_id", "event_id"})
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "date_time", nullable = false)
    @NonNull
    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NonNull
    private Location location;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NonNull
    private Event event;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Performance that = (Performance) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
