package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ArtistMapper {

    ArtistDto artistToArtistDto(Artist artist);

    List<ArtistDto> artistToArtistDto(List<Artist> artist);

    Artist artistDtoToArtist(ArtistDto artistDto);
}
