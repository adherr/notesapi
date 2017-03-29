package herr.andrew.notesapi.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.sql.DataSource;

import herr.andrew.notesapi.database.NotesDatabaseActor;

/**
 * Configures the database and starts the Jersey server
 */
public class NotesApiStarter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesApiStarter.class);
    private static final String PROPERTIES_FILE = "NotesApi.properties";

    /** Create the application and start it */
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        }

        LOGGER.info("Setting up database");
        setupDatabaseActor();

        LOGGER.info("Configuring and starting the server");
        GrizzlyHttpServerFactory.createHttpServer(URI.create(props.getProperty("SERVER_BASE_URI")),
                new NotesApiApplication());
    }

    private static void setupDatabaseActor() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.setConnectionTestQuery("VALUES 1");
        config.addDataSourceProperty("URL", "jdbc:h2:file:./notesdb");
        config.addDataSourceProperty("user", "sa");
        DataSource ds = new HikariDataSource(config);
        NotesDatabaseActor.setActor(new NotesDatabaseActor(ds));
    }

}
