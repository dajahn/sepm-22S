package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
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
public class SeatTicket extends Ticket {

    @Column(name = "sector_id")
    @NonNull
    private Long sectorId;

    @ManyToOne
    @JoinColumn(name = "sector_id", insertable = false, updatable = false)
    private SeatSector sector;

    @Column(name = "seat_id")
    @NonNull
    private Long seatId;

    @ManyToOne
    @JoinColumn(name = "seat_id", insertable = false, updatable = false)
    private Seat seat;

    public SeatTicket(@NonNull Long performanceId, @NonNull Long orderId, @NonNull Long sectorId, @NonNull Long seatId) {
        super(performanceId, orderId);
        this.sectorId = sectorId;
        this.seatId = seatId;
    }

    @Override
    public SeatTicketDto mapToDto(TicketMapper mapper) {
        return mapper.seatTicketToSeatTicketDto(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        SeatTicket that = (SeatTicket) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
