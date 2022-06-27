package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationInvoice extends Invoice {

    @OneToOne
    @JoinColumn(name = "cancellation_id")

    @NonNull
    private Cancellation cancellation;

    @Override
    public InvoiceType getType() {
        return InvoiceType.CANCELLATION;
    }

    @Override
    public User getUser() {
        return cancellation.getUser();
    }

    @Override
    public List<Ticket> getTickets() {
        return cancellation.getTickets();
    }
}
