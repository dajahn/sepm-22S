package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedPerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.Mapper;
import java.util.List;
import org.mapstruct.Named;

@Mapper(uses = {SectorMapper.class, FileMapper.class})
public interface PerformanceMapper {

    PerformanceDto performanceToPerformanceDto(Performance performance);

    List<PerformanceDto> performanceToPerformanceDto(List<Performance> performances);

    @Named("detailed")
    DetailedPerformanceDto performanceToDetailedPerformanceDto(Performance performance);
}
