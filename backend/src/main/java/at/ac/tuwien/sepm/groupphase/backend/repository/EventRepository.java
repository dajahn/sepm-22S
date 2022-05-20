package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Returns all Events matching the name substring.
     *
     * @param name substring of the searche event name
     * @return List of Events
     */
    List<Event> findByNameContaining(@Param("name") String name);

    List<Event> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("select e from Event e join fetch e.performances where e.id = ?1")
    Optional<Event> findById(Long id);
}
