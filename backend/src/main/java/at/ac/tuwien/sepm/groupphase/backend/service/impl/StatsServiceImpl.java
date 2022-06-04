package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StatsDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.NewsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class StatsServiceImpl implements StatsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public StatsServiceImpl(NewsRepository newsRepository, EventRepository eventRepository, TicketRepository ticketRepository) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }


    @Override
    public StatsDto getStats() {
        LOGGER.trace("getStats");
        return new StatsDto(
            ticketRepository.findAll().size(),
            eventRepository.findAll().size(),
            newsRepository.findAll().size()
        );
    }
}
