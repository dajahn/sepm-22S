package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapper {

    EventDto eventToEventDto(Event event);

    Event eventDtoToEvent(EventDto eventDto);
}
