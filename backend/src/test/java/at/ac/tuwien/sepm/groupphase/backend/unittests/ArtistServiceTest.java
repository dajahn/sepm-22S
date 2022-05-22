package at.ac.tuwien.sepm.groupphase.backend.unittests;


import at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ArtistServiceTest implements ArtistTestData {
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private ArtistService artistService;


    @Test
    public void givenArtistsInDb_whenFindById_thenFindArtist() {
        Artist a = artistRepository.save(new Artist(ARTIST_NAME, ARTIST_DESCRIPTION));
        Artist b = artistService.findById(a.getId());

        assertEquals(a, b);
    }

    @Test
    public void givenNoMatchingArtistInDb_whenFindById_throwNotFoundException() {
        assertThrows(NotFoundException.class, () ->  artistService.findById(-1L));
    }

    @Test
    public void givenArtistsInDb_whenSearchByName_thenFindListOfMatchingArtists() {
        artistRepository.save(new Artist(ARTIST_NAME + "ZZZZ", ARTIST_DESCRIPTION));
        artistRepository.save(new Artist(ARTIST_NAME + "ZZZ", ARTIST_DESCRIPTION));
        artistRepository.save(new Artist(ARTIST_NAME + "YYY", ARTIST_DESCRIPTION));
        ArtistSearchDto searchDto = new ArtistSearchDto("ZZ", 10);

        List<Artist> artists = artistService.find(searchDto);
        assertEquals(2, artists.size());
        for (Artist a : artists) {
            assertTrue(a.getName().contains("ZZ"));
        }
    }
}
