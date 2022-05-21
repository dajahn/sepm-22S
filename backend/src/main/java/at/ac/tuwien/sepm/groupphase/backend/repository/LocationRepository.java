package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Returns all locations with the name parameter contained in their name.
     *
     * @param name     to search for
     * @param pageable maximum amount of locations to return
     * @return returns a list of locations with matching name, at maximum a specified number
     */
    List<Location> findByNameContaining(@Param("name") String name, Pageable pageable);

    /**
     * Returns all locations with the name parameter contained in their name.
     *
     * @param name to search for
     * @return a list of all locations with matching name
     */
    List<Location> findByNameContaining(@Param("name") String name);

    @Query(value = "select * from location l "
        + "where (UPPER(l.name) like UPPER(:name) or :name is null) "
        + "and (UPPER(l.city) like UPPER(:city) or :city is null) "
        + "and (l.country = :country or :country is null) "
        + "and (l.zip_Code like :zipCode or :zipCode is null) "
        + "and (UPPER(l.street) like UPPER(:street) or :street is null)", nativeQuery = true)
    List<Location> findAllBy(@Param("name") String name, @Param("city") String city, @Param("country") int country, @Param("zipCode") String zipCode, @Param("street") String street);
}

