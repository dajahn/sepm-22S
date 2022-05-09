package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedPerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;

@Mapper(uses = SectorMapper.class)
public interface PerformanceMapper {

    DetailedPerformanceDto performanceToDetailedPerformanceDto(Performance performance);
}
