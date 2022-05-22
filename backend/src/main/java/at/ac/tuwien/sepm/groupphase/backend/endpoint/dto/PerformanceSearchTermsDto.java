package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PerformanceSearchTermsDto {
    private String fromDate;
    private String toDate;

    private String eventName;
    private String locationName;
    private double price;

}

