package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This entity represents an event, consisting of multiple performances.
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 127)
    private String name;

    @Column(nullable = false, length = 1023)
    private String description;

    @Column(nullable = false)
    private Duration duration;

    @OneToOne
    @JoinColumn(name = "thumbnail_id")
    private File thumbnail;

    @Column(nullable = false)
    private EventCategory category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "holding",
        joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"))
    @OrderBy("name")
    private Set<Artist> artists;

    @OneToMany(cascade = {CascadeType.ALL})
    @ToString.Exclude
    private List<Performance> performances;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
