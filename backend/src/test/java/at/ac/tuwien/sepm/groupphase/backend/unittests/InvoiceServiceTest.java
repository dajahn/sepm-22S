package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.enums.InvoiceType;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class InvoiceServiceTest implements TestData {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void whenNewInvoiceIsCreated_thenNewInvoiceIsStoredInTheDatabase() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Invoice invoice = new Invoice(InvoiceType.NORMAL);
            invoiceService.create(invoice);
            System.out.println(invoice);
        }
        System.out.println(invoiceRepository.findAllByOrderByIdDesc());
        Thread.sleep(10000); // wait for pdfs to be generated
        System.out.println(invoiceRepository.findAllByOrderByIdDesc());
    }

    @Test
    public void whenInvoiceIsCanceled_thenCancellationInvoiceIsCreated() throws InterruptedException {
        Invoice invoice = new Invoice(InvoiceType.NORMAL);
        invoiceService.create(invoice);

        System.out.println(invoice);

        Invoice cancellation = invoiceService.cancel(invoice);
        System.out.println(invoice);
        System.out.println(cancellation);

        Thread.sleep(10000); // wait for pdfs to be generated

        System.out.println(invoice);
        System.out.println(cancellation);
    }
}
