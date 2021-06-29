DROP TABLE IF EXISTS firestations;
 
CREATE TABLE firestations (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  adress VARCHAR(250) NOT NULL,
  station INT NOT NULL
);
 
INSERT INTO firestations (adress, station) VALUES
  ('adress1', 1),
  ('adress2', 2),
  ('adress3', 3);