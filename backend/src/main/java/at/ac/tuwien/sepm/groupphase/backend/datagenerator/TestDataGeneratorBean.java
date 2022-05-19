package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

@Component
@Profile("test")
public class TestDataGeneratorBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DataSource dataSource;

    /**
     * Executed once when the component is instantiated. Inserts some data to start testing from.
     */
    public TestDataGeneratorBean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void generateData() throws SQLException {
        LOGGER.info("Generating database ...");
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/cleanup.sql"));
            LOGGER.info("Cleared database.");
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("sql/test-data.sql"));
            LOGGER.info("Initialized database.");
        }
    }
}
