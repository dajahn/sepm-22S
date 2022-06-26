package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    @Query(
        value = "SELECT * FROM USER WHERE STATUS = 1", nativeQuery = true
    )
    List<User> loadLockedUser();

    @Query(
        value = "SELECT * from USER u where (:status = -1 or u.status = :status)"
            + " and (:role = -1 or u.role = :role ) "
            + " and (:nameString is null or u.email like CONCAT('%',:nameString,'%'))", nativeQuery = true)
    List<User> loadUsers(@Param("nameString") String nameString, @Param("role") int role, @Param("status") int status, Pageable pageable);


    @Query(
        value = "SELECT count(id) from USER u where (:status = -1 or u.status = :status)"
            + " and (:role = -1 or u.role = :role ) "
            + " and (:nameString is null or u.email like CONCAT('%',:nameString,'%'))", nativeQuery = true)
    Long getMatchingUsersCount(@Param("nameString") String nameString, @Param("role") int role, @Param("status") int status);
}
