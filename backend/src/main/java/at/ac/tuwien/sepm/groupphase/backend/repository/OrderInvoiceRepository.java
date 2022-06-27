package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.OrderInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderInvoiceRepository extends JpaRepository<OrderInvoice, Long> {

    /**
     * Find invoice for given order id.
     *
     * @param id order id
     * @return found invoice
     */
    List<Invoice> findAllByOrderId(@Param("id") long id);
}

