package at.ac.tuwien.sepm.groupphase.backend.entity;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.CartService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StandingTicket extends Ticket {

    @Column(name = "sector_id")
    @NonNull
    private Long sectorId;

    @ManyToOne
    @JoinColumn(name = "sector_id", insertable = false, updatable = false)
    private StandingSector sector;

    public StandingTicket(@NonNull Long performanceId, @NonNull Long orderId, @NonNull Long sectorId) {
        super(performanceId, orderId);
        this.sectorId = sectorId;
    }

    @Override
    public StandingTicketDto mapToDto(TicketMapper mapper) {
        return mapper.standingTicketToStandingTicketDto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        StandingTicket that = (StandingTicket) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
