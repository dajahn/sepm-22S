package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Profile({"generateSql"})
@Component
public class SqlFileGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DataSource dataSource;

    public SqlFileGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PreDestroy
    private void generateSql() throws SQLException {
        LOGGER.info("Dumping database to SQL file");

        Connection con = dataSource.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeQuery("SCRIPT TO './src/main/resources/sql/test-data.sql'");

    }

}
