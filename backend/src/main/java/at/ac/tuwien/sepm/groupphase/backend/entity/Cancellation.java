package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    @NonNull
    private LocalDateTime dateTime;

    @Column(name = "user_id")
    @NonNull
    private Long userId;

    @ManyToOne
    @JoinColumn(
        name = "user_id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_cancellation_user", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL")
    )
    private User user;

    @OneToMany(fetch = javax.persistence.FetchType.EAGER, cascade = javax.persistence.CascadeType.ALL, mappedBy = "cancellation")
    @NonNull
    @OrderColumn(name = "ticket_order")
    private List<Ticket> tickets;

    @OneToOne
    private CancellationInvoice invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Cancellation cancellation = (Cancellation) o;
        return id != null && Objects.equals(id, cancellation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
