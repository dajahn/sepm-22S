package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /**
     * Returns all artists with the name parameter contained in their name
     * @param name to search for
     * @param pageable maximum amount of artists to return
     * @return returns a list of artists with matching name, at maximum a specified number
     */
    List<Artist> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Returns all artists with the name parameter contained in their name
     * @param name to search for
     * @return a list of all artists with matching name
     */
    List<Artist> findByNameContaining(@Param("name") String name);

}
