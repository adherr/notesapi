package herr.andrew.notesapi.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import herr.andrew.notesapi.database.NotesDatabaseActor;
import herr.andrew.notesapi.structures.Note;

/**
 * API endpoints for the Notes resource
 */
@Path("api/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Create the Note in the database and return it */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Note createNote(Note bodyOnly) throws SQLException {
        logger.info("Creating note with body {}", bodyOnly.getBody());
        return NotesDatabaseActor.getActor().createNote(bodyOnly.getBody());
    }

    /** Get an existing Note */
    @GET
    @Path("{id}")
    public Note getNote(@PathParam("id") int id) throws SQLException {
        logger.info("Getting note with id {}", id);
        return NotesDatabaseActor.getActor().getNote(id);
    }

    /** Get all of the existing Notes */
    @GET
    public List<Note> getAllNotes(@DefaultValue("") @QueryParam("query") String query) throws SQLException {
        logger.info("Getting all notes that contain \"{}\"", query);
        return NotesDatabaseActor.getActor().getAllNotes(query);
    }
}
