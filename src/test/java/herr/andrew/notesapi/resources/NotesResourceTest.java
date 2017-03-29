package herr.andrew.notesapi.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import herr.andrew.notesapi.database.NotesDatabaseActor;
import herr.andrew.notesapi.server.NotesApiApplication;
import herr.andrew.notesapi.structures.Note;

/**
 * Unit tests for {@link NotesResource}.
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class NotesResourceTest extends JerseyTest {
    @Mock
    NotesDatabaseActor actor;

    @Override
    protected Application configure() {
        return new NotesApiApplication();
    }

    /** Insert the mock into the locator before each test */
    @Before
    public void setupDatabaseActorLocator() {
        NotesDatabaseActor.setActor(actor);
    }

    /** Test that we call the correct method in the database actor and return a Note */
    @Test
    public void createShouldReturnANote() throws Exception {
        String body = "test this";
        String jsonData = String.format("{\"body\" : \"%s\"}", body);
        Note expectedNote = new Note(1, body);
        when(actor.createNote(body)).thenReturn(expectedNote);
        Note returnedNote = target("api/notes").request(MediaType.APPLICATION_JSON).buildPost(Entity.json(jsonData))
                .invoke(Note.class);
        verify(actor).createNote(body);
        assertEquals(expectedNote, returnedNote);
    }

    /** Test that getNote calls the proper database method and returns a note */
    @Test
    public void getNoteShouldReturnANote() throws Exception {
        int id = 5;
        Note note = new Note(id, "get this");
        when(actor.getNote(id)).thenReturn(note);
        Note returnedNote = target("api/notes/" + id).request().get(Note.class);
        verify(actor).getNote(id);
        assertEquals(note, returnedNote);
    }

    /** Test that we get a 500 if we ask for an id that doesn't exist */
    @Test
    public void getNoteCanThrow() throws Exception {
        int id = 2544723;
        when(actor.getNote(id)).thenThrow(SQLException.class);
        Response response = target("api/notes/" + id).request().get();
        assertEquals(500, response.getStatus());
    }

    /** Test that getAllNotes calls the correct method in the database and returns a list of Notes */
    @Test
    public void getAllNotesReturnsListOfAllNotes() throws SQLException {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Pick up milk!"));
        notes.add(new Note(2, "Ask Larry about the TPS reports."));
        when(actor.getAllNotes("")).thenReturn(notes);
        List<Note> returnedNotes = target("api/notes/").request()
                .get(new GenericType<List<Note>>() {/* empty generic type definition */});
        verify(actor).getAllNotes("");
        assertEquals(notes, returnedNotes);
    }

    /** Test that getAllNotes with a query param calls the correct method in the database and returns a list of Notes */
    @Test
    public void getAllNotesReturnsListOfAllNotesContainingString() throws SQLException {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Pick up milk!"));
        when(actor.getAllNotes("milk")).thenReturn(notes);
        List<Note> returnedNotes = target("api/notes/").queryParam("query", "milk").request()
                .get(new GenericType<List<Note>>() {/* empty generic type definition */});
        verify(actor).getAllNotes("milk");
        assertEquals(notes, returnedNotes);
    }
}
