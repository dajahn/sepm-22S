package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class SeatDto {

    private Long id;
    private Integer row;
    private Integer column;
    private PointDto point;
}
