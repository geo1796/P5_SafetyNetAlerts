DROP TABLE IF EXISTS firestations;
 
CREATE TABLE firestations (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  address VARCHAR(250) NOT NULL,
  station INT NOT NULL
);
