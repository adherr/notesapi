package herr.andrew.notesapi.structures;

/**
 * The Note object as specified in the assessment. Includes an id for use with database persistence.
 */
public class Note {
    private int id;
    private String body;

    /**
     * Create an empty Note
     */
    public Note() {
        // required Java bean constructor for Jackson serialization
    }

    /**
     * Create a fully specified note.
     * 
     * @param id
     *        of the note (possibly in the database)
     * @param body
     *        of the note
     */
    public Note(int id, String body) {
        super();
        this.id = id;
        this.body = body;
    }

    /**
     * Return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id
     *
     * @param id
     *        the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the body
     *
     * @param body
     *        the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.format("Note [id=%s, body=%s]", id, body);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (body == null ? 0 : body.hashCode());
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Note other = (Note) obj;
        if (body == null) {
            if (other.body != null) {
                return false;
            }
        } else if (!body.equals(other.body)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        return true;
    }

}
