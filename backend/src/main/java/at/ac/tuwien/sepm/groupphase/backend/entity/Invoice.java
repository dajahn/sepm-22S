package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.entity.converter.InvoiceIdConverter;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceStatus;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @OneToMany
    @JoinColumn
    @NonNull
    private List<Ticket> tickets;

    @Column(nullable = false)
    @NonNull
    private InvoiceStatus status = InvoiceStatus.CREATED;

    @Column(nullable = false)
    @NonNull
    private InvoiceType type;

    @Convert(converter = InvoiceIdConverter.class)
    @Column(nullable = false)
    private InvoiceId identification;

    /**
     * Reference to an invoice.
     * Meaning defined by type:
     * - CANCELLATION ==> referencing the initial invoice
     * - CANCELED ==> referencing the cancellation invoice
     * - NORMAL ==> no meaning
     */
    @OneToOne
    @JoinColumn(referencedColumnName = "id")
    @ToString.Exclude
    private Invoice reference;

    /**
     * Used for displaying the reference when executing toString without creating a stackoverflow.
     *
     * @return String representation of the reference
     */
    @ToString.Include(name = "reference")
    private String toStringAttachReference() {
        return this.reference == null ? null : Invoice.class.getSimpleName() + "(id=" + this.reference.getId() + ")";
    }

    @OneToOne
    @JoinColumn
    private File pdf;

    //TODO: remove when switching to an oop approach
    @ManyToOne
    private User user;

    //TODO: move when switching to an oop approach
    @OneToOne
    @JoinColumn
    private TicketOrder order;

    //TODO: move when switching to an oop approach
    @OneToOne
    @JoinColumn
    private Cancellation cancellation;

    @Column(nullable = false)
    @NonNull
    private LocalDate date = LocalDate.now();

    public Invoice(TicketOrder order) {
        this.tickets = new ArrayList<>(order.getTickets());
        this.type = InvoiceType.NORMAL;
        this.user = order.getUser();
        this.order = order;
    }

    public Invoice(Cancellation cancellation) {
        this.tickets = Collections.singletonList(cancellation.getTicket());
        this.type = InvoiceType.CANCELLATION;
        this.user = cancellation.getTicket().getOrder().getUser();
        this.cancellation = cancellation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        return id != null && Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
