package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

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
    private Image thumbnail;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "holding",
        joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"))
    @OrderBy("name")
    private List<Artist> artists;

    @OneToMany(cascade = {CascadeType.ALL})
    @ToString.Exclude
    private List<Performance> performances;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
