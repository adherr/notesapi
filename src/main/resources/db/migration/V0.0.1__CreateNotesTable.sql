/* SQL migration to create the notes table */

CREATE TABLE notes (
  id INT NOT NULL AUTO_INCREMENT,
  body TEXT,
  PRIMARY KEY (id)
); 