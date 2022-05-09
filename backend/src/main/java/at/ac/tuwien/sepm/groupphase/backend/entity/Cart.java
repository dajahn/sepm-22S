package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
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
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Cart {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @MapsId
    private User user;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @OneToMany
    @ToString.Exclude
    private List<Ticket> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Cart cart = (Cart) o;
        return user != null && Objects.equals(user, cart.user);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
