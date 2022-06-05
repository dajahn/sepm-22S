package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class PagedNewsDto {
    List<NewsDto> news;
    Long totalCount;
}
