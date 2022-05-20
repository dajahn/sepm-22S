package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "select * from Event e " +
        "where (e.category = :category or :category = -1) " +
        "and (UPPER(e.description) like UPPER(:description) or :description is null) " +
        "and (e.duration = :duration or :duration is null) " +
        "and (UPPER(e.name) like UPPER(:name) or :name is null)", nativeQuery = true)
    List<Event> findAllBy(@Param("category") int category, @Param("description") String description, @Param("duration") Duration duration, @Param("name") String name);
}
