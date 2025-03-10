package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @NonNull
    @Column(nullable = false, name = "title")
    private String title;

    @NonNull
    @Column(nullable = false, name = "description", length = 1023)
    private String description;

    @NonNull
    @Column()
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Event event;

    @NonNull
    @Column()
    private String imageDescription;

    @NonNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private File file;

    @ManyToMany(mappedBy = "readNews", fetch = FetchType.EAGER)
    Set<User> readBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        News news = (News) o;
        return id != null && Objects.equals(id, news.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
