package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USER;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class NewsServiceTest {
    @Autowired
    private NewsService newsService;

    @Test
    @Transactional
    @Rollback
    public void givenNothing_whenFindByInvalidId_thenNotFoundException(){
        assertThrows(
            NotFoundException.class, () -> newsService.getById(100L,ADMIN_USER)
        );
    }
}
