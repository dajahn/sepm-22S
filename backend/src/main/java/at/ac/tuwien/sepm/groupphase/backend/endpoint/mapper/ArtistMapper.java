package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ArtistMapper {

    ArtistMapper INSTANCE = Mappers.getMapper( ArtistMapper.class );


    ArtistDto artistToArtistDto(Artist artist);

    Artist artistDtoToArtist(ArtistDto artistDto);

    List<ArtistDto> artistToArtistDto(List<Artist> artist);
}
