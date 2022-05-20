package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    /**
     * finds a valid password reset request with a given hash.
     *
     * @param hash the hash identifying the password reset request.
     * @return the target password reset request or null
     */
    @Query("select p from PasswordReset p where p.hash = ?1 and p.used = FALSE and (p.validUntil > current_date)")
    PasswordReset findByHash(UUID hash);

}
