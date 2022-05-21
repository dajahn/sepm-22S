package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;


@Mapper(uses = {PerformanceMapper.class, FileMapper.class})
public interface EventMapper {

    @Named("detailed")
    DetailedEventDto eventToDetailedEventDto(Event event);

    EventDto eventToEventDto(Event event);

    List<EventDto> eventToEventDto(List<Event> events);

    Event eventDtoToEvent(EventDto eventDto);
}
