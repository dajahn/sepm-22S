package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;
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
@NoArgsConstructor
@RequiredArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 127)
    @NonNull
    private String name;

    @Column(nullable = false, length = 1023)
    @NonNull
    private String description;

    @Column(nullable = false)
    @NonNull
    private Duration duration;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "thumbnail_id")
    @ToString.Exclude
    private File thumbnail;

    @Column(nullable = false)
    @NonNull
    private EventCategory category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "holding",
        joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"))
    @OrderBy("name")
    @NonNull
    private Set<Artist> artists;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "event")
    @OrderBy("dateTime ASC")
    @ToString.Exclude
    @NonNull
    private List<Performance> performances = new java.util.ArrayList<>();

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
