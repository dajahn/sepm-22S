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
public class OrderInvoice extends Invoice {

    @OneToOne
    @JoinColumn(name = "order_id")
    @NonNull
    private TicketOrder order;

    @Override
    public InvoiceType getType() {
        return InvoiceType.NORMAL;
    }

    @Override
    public User getUser() {
        return order.getUser();
    }

    @Override
    public List<Ticket> getTickets() {
        return order.getTickets();
    }
}
