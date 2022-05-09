package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.entity.File;

import java.util.List;

public record EventDto(Long id, String name, String description, String duration, File thumbnail, String category,
                       List<ArtistDto> artists, List<PerformanceDto> performances) {
}
