package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /**
     * Find all invoice entries ordered by id (descending).
     *
     * @return ordered list of al invoice entries
     */
    List<Invoice> findAllByOrderByIdDesc();

    /**
     * Find last invoice ordered by id (descending).
     *
     * @return last invoice
     */
    Invoice findFirstByOrderByIdDesc();

    /**
     * Find normal invoice for given order.
     *
     * @return found invoice
     */
    List<Invoice> findAllByOrderIdAndType(@Param("id") long id, @Param("type") InvoiceType type);

}

