package herr.andrew.notesapi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import herr.andrew.notesapi.structures.Note;

/**
 * Takes care of all database interactions in the notes table. A hand written ORM.
 * 
 * Implements the locator pattern for {@link NotesDatabaseActor} in order to have a single instance that will reuse the
 * DataSource from the connection pool. Useful for testing.
 */
public class NotesDatabaseActor {
    private static final String INSERT_STATEMENT = "INSERT INTO notes (body) VALUES (?)";
    private static final String GET_STATEMENT = "SELECT * FROM notes WHERE id = ?";
    private static final String GET_ALL_STATEMENT = "SELECT * FROM notes WHERE body LIKE ?";
    private static NotesDatabaseActor actor;
    private final DataSource dataSource;

    /**
     * Create a new database actor using the given datasource.
     * 
     * @param dataSource
     *        to get connections to the database from
     */
    public NotesDatabaseActor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Set the actor in the locator.
     * 
     * @param actor
     *        to set
     */
    public static void setActor(NotesDatabaseActor actor) {
        NotesDatabaseActor.actor = actor;
    }

    /**
     * Get the actor from the locator.
     * 
     * @return the instance of the {@link NotesDatabaseActor} in the locator
     */
    public static NotesDatabaseActor getActor() {
        return actor;
    }

    /**
     * Creates a new note in the database and returns the corresponding {@link Note} object.
     * 
     * @param body
     *        to create a new note for
     * @return the Note object representing the new note in the database
     * @throws SQLException
     *         if there are JDBC errors, or if there was some problem creating the row in the database
     */
    public Note createNote(String body) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_STATEMENT,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, body);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating note failed, no rows affected");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Note(generatedKeys.getInt(1), body);
                } else {
                    throw new SQLException("Creating note failed, no new id returned");
                }
            }
        }
    }

    /**
     * Returns a note with the specified id from the database.
     * 
     * @param id
     *        to get
     * @return the Note object with the id given and body retrieved from the database
     * @throws SQLException
     *         if there are any JDBC errors or if the id is not found in the database
     */
    public Note getNote(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_STATEMENT)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new Note(id, results.getString("body"));
                } else {
                    throw new SQLException(String.format("No note with id %d found in the database", id));
                }
            }
        }
    }

    /**
     * Return all of the rows from the notes table whose body contains the queryString. Call with the empty string to
     * get all notes.
     * 
     * @param query
     *        to look for in the body to restrict returned notes. Pass "" to get all notes
     * @return a List of Note objects represented by the notes table. If there are no notes, this will be an empty list.
     * @throws SQLException
     *         if there are JDBC errors
     */
    public List<Note> getAllNotes(String query) throws SQLException {
        List<Note> allNotes = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_ALL_STATEMENT)) {
            statement.setString(1, "%" + likeSanitize(query) + "%");
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    allNotes.add(new Note(results.getInt("id"), results.getString("body")));
                }
            }
        }
        return allNotes;
    }

    /** Shamelessly stolen from http://stackoverflow.com/questions/327765/wildcards-in-java-preparedstatements */
    private String likeSanitize(String input) {
        return input.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
    }
}
