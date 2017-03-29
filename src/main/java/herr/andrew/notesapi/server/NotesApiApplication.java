package herr.andrew.notesapi.server;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Level;

/**
 * A Jersey {@link ResourceConfig}/JAX-RS Application configuration.
 */
public class NotesApiApplication extends ResourceConfig {
    private static final String RESOURCE_PACKAGE = "herr.andrew.notesapi.resources";
    private static final String PROVIDER_PACKAGE = "herr.andrew.notesapi.providers";
    private static final Logger LOGGER = LoggerFactory.getLogger(NotesApiApplication.class);

    static {
        /* Replace Jersey's java.util.logging output with slf4j so that we don't have two different loggers in use. I
         * like to do this in the ResourceConfig so that logs look correct in unit tests */
        java.util.logging.Logger.getLogger("").setLevel(Level.ALL);
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Construct a new instance of the Jersey application.
     */
    public NotesApiApplication() {
        LOGGER.info("Adding resource package {} to ResourceConfig", RESOURCE_PACKAGE);
        packages(RESOURCE_PACKAGE);
        LOGGER.info("Adding provider package {} to ResourceConfig", PROVIDER_PACKAGE);
        packages(PROVIDER_PACKAGE);

        LOGGER.info("Registering Jackson for JSON");
        register(JacksonFeature.class);

        LOGGER.info("Registering traffic logger");
        // Traffic logs are nice for debugging, and we can put them in a separate file with log4j2
        register(new LoggingFeature(java.util.logging.Logger.getLogger("herr.andrewh.notesapi.Traffic"),
                java.util.logging.Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, 1024));

        // usually I set up Swagger here
    }

}
