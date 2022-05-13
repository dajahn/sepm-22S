package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile("generateData")
@Component
public class ArtistDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_Artists_TO_GENERATE = 5;
    private static final String TEST_ARTISTS_NAME = "Artist #";
    private static final String TEST_ARTISTS_DESCRIPTION = "Summary of the artists description #";
    private final ArtistRepository artistRepository;

    public ArtistDataGenerator(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @PostConstruct
    private void generateArtists() {
        if (artistRepository.count() > 0) {
            LOGGER.trace("generateArtists artists already generated");
        } else {
            LOGGER.trace("generateArtists generating {} Artists", NUMBER_OF_Artists_TO_GENERATE);

            Artist artist;
            for (int i = 0; i < NUMBER_OF_Artists_TO_GENERATE; i++) {
                artist = new Artist();
                artist.setDescription(TEST_ARTISTS_DESCRIPTION + i);
                artist.setName(TEST_ARTISTS_NAME + i);
                artistRepository.save(artist);
            }
        }

    }
}
