package herr.andrew.notesapi.providers;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/** Provider to return a 500 with a JSON object if anything throws */
@Provider
public class UncaughtThrowableExceptionMapper implements ExtendedExceptionMapper<Throwable> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean isMappable(Throwable throwable) {
        // Jersey should handle WebApplicationException, we should not interfere with these
        return !(throwable instanceof WebApplicationException);
    }

    /** Log the exception, then return a 500 with the message */
    @Override
    public Response toResponse(Throwable throwable) {
        logger.error("Uncaught exception", throwable);
        return Response.serverError().entity(Entity.json(String.format("{\"error\" : \"%s\"}", throwable.getMessage())))
                .build();
    }
}