package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Returns all locations with the name parameter contained in their name
     * @param name to search for
     * @param pageable maximum amount of locations to return
     * @return returns a list of locations with matching name, at maximum a specified number
     */
    List<Location> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Returns all locations with the name parameter contained in their name
     * @param name to search for
     * @return a list of all locations with matching name
     */
    List<Location> findByNameContaining(@Param("name") String name);
}
