package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedEventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.FileDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TopTenEventDto;
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

    //Maps event, and it's ticket count to one dto.
    default TopTenEventDto eventToTopTenEventDto(Event event, int ticketCount) {
        TopTenEventDto result = new TopTenEventDto();

        FileDto fileDto = new FileDto();
        fileDto.setType(event.getThumbnail().getType());
        fileDto.setUrl("/files/" + event.getThumbnail().getId().toString());
        result.setThumbnail(fileDto);

        result.setName(event.getName());
        result.setId(event.getId());
        result.setCategory(event.getCategory());
        result.setTicketCount(ticketCount);
        return result;
    }
}
