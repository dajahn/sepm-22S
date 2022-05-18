package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.entity.converter.InvoiceIdConverter;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceStatus;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn
    // TODO add @NotNull after merge
    private TicketOrder order;

    @Column(nullable = false)
    @NonNull
    private InvoiceStatus status = InvoiceStatus.CREATED;

    @Column(nullable = false)
    @NonNull
    private InvoiceType type;

    @Convert(converter = InvoiceIdConverter.class)
    @Column(nullable = false)
    private InvoiceId identification;

    @ManyToOne
    @JoinColumn
    private File pdf;

    @Column(nullable = false)
    @NonNull
    private LocalDate date = LocalDate.now();

    public Invoice(TicketOrder order, InvoiceType type) {
        this.order = order;
        this.type = type;
    }

}
