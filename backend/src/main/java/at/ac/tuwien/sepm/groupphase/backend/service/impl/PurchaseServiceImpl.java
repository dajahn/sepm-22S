package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PagedTicketsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Cancellation;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.enums.OrderType;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CancellationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import at.ac.tuwien.sepm.groupphase.backend.service.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final TicketRepository ticketRepository;
    private final CancellationRepository cancellationRepository;
    private final InvoiceService invoiceService;
    private final TicketMapper ticketMapper;
    private final EntityManager entityManager;

    public PurchaseServiceImpl(
        TicketRepository ticketRepository,
        CancellationRepository cancellationRepository,
        InvoiceService invoiceService,
        TicketMapper ticketMapper,
        EntityManager entityManager
    ) {
        this.ticketRepository = ticketRepository;
        this.cancellationRepository = cancellationRepository;
        this.invoiceService = invoiceService;
        this.ticketMapper = ticketMapper;
        this.entityManager = entityManager;
    }

    @Override
    public List<Ticket> getUpcomingPurchasedTickets(Long userId) {
        LOGGER.trace("getUpcomingPurchasedTickets(Long userId) for user with id: {}", userId);
        return ticketRepository.findByOrderUserIdAndOrderTypeAndPerformanceDateTimeAfterOrderByPerformanceDateTime(
            userId, OrderType.PURCHASE, LocalDateTime.now()
        );
    }

    @Override
    public PagedTicketsDto getPastPurchasedTickets(Long userId, int page, int size) {
        LOGGER.trace("getPastPurchasedTickets(Long userId) for user with id: {}", userId);
        Long totalCount = ticketRepository.countByOrderUserIdAndOrderTypeAndPerformanceDateTimeLessThanEqual(userId, OrderType.PURCHASE, LocalDateTime.now());
        Pageable pageable = PageRequest.of(page, size);
        Page<Ticket> tickets = ticketRepository.findByOrderUserIdAndOrderTypeAndPerformanceDateTimeLessThanEqualOrderByPerformanceDateTime(
            userId, OrderType.PURCHASE, LocalDateTime.now(), pageable
        );
        List<TicketDto> ticketDtos = tickets.stream().map(this.ticketMapper::ticketToTicketDto).toList();
        return new PagedTicketsDto(ticketDtos, totalCount);
    }

    @Override
    @Transactional
    public void cancel(Long userId, List<Long> ticketIds) {
        LOGGER.trace("cancel(Long userId, Long ticketId) with userId = {} and ticketIds = {}", userId, ticketIds);

        if (ticketIds == null || ticketIds.isEmpty()) {
            throw new ValidationException("Cannot cancel zero tickets.");
        }

        List<Ticket> tickets = ticketRepository.findByOrderTypeAndOrderUserIdAndIdIn(OrderType.PURCHASE, userId, ticketIds);
        if (tickets.size() != ticketIds.size()) {
            throw new NotFoundException("At least one ticket you wanted to cancel was not found.");
        }

        Cancellation cancellation = new Cancellation(LocalDateTime.now(), userId, new ArrayList<>(tickets));
        cancellation = cancellationRepository.save(cancellation);

        for (Ticket ticket : tickets) {
            if (ticket.getCancellation() != null) {
                throw new ValidationException("At least one ticket has already been cancelled.");
            }
            if (LocalDateTime.now().isAfter(ticket.getPerformance().getDateTime())) {
                throw new ValidationException("The performance of at least one ticket is already over, it cannot be cancelled.");
            }
            ticket.setCancellationId(cancellation.getId());
        }

        ticketRepository.saveAll(tickets);
        entityManager.flush();
        entityManager.refresh(cancellation);

        invoiceService.create(cancellation);
    }

}
