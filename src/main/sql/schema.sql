CREATE TABLE DICTIONARY_VERSION (
  VALUE VARCHAR(20) NOT NULL
);

CREATE TABLE LEMMA (
  TEXT VARCHAR(100) NOT NULL ,
  JSON VARCHAR(4000) NOT NULL ,
  ID INT NOT NULL,
  REVISION INT NULL,
  STATE TINYINT NOT NULL,
  PRIMARY KEY (ID)
);

-- do not intersect with opercorpora id which is positive
CREATE SEQUENCE LEMMA_ID START WITH -1 INCREMENT BY -1;
