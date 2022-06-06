package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class StatsDto {
    private long tickets;
    private long events;
    private long news;

    public StatsDto(long tickets, long events, long news) {
        this.tickets = tickets;
        this.events = events;
        this.news = news;
    }
}

