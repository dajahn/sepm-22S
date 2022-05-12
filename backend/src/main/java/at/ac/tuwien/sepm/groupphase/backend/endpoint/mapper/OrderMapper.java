package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CartDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.TicketOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = TicketMapper.class)
public interface OrderMapper {

    CartDto orderToCartDto(TicketOrder order);
}
