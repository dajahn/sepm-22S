package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile({"generateData", "test"})
@Component
public class ArtistDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ARTISTS_TO_GENERATE = 100;

    private final ArtistRepository artistRepository;

    public ArtistDataGenerator(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @PostConstruct
    private void generateEvents() {
        if (artistRepository.count() > 0) {
            LOGGER.debug("Artists already generated");
        } else {
            for (int i = 0;
                 i < NUMBER_OF_ARTISTS_TO_GENERATE;
                 i++) {
                Faker faker = new Faker();
                Artist artist = new Artist(faker.artist().name(), faker.lorem().paragraph());
                LOGGER.debug("Saving artist {}", artist);
                artistRepository.save(artist);
            }
        }
    }

}
