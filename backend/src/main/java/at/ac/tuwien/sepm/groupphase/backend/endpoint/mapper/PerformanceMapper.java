package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedPerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(uses = {SectorMapper.class, FileMapper.class})
public interface PerformanceMapper {

    PerformanceDto performanceToPerformanceDto(Performance performance);

    @Named("detailed")
    DetailedPerformanceDto performanceToDetailedPerformanceDto(Performance performance);
}
