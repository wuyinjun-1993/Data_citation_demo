CREATE TABLE author (
  name varchar(100) NOT NULL,
  paper_key varchar(200) NOT NULL,
  current_affiliation varchar(200),
  website varchar(200),
  CONSTRAINT author_pk PRIMARY KEY (name, paper_key),
  CONSTRAINT author_paper_fk FOREIGN KEY (paper_key) REFERENCES paper(paper_key)
);

CREATE TABLE paper (
  paper_key varchar(200) NOT NULL,
  title text NOT NULL,
  year int(11),
  pages varchar(50),
  conference text,
  conference_key varchar(100) NOT NULL,
  CONSTRAINT paper_pk PRIMARY KEY (paper_key),
  CONSTRAINT paper_pdf_fk FOREIGN KEY (paper_key) REFERENCES paper_pdf(paper_key),
  CONSTRAINT paper_conference_fk FOREIGN KEY (conference_key) REFERENCES proceedings(conference_key)
);

CREATE TABLE paper_pdf (
  paper_key varchar(200) NOT NULL,
  pdf varchar(200),
  CONSTRAINT paper_pk PRIMARY KEY (paper_key)
);

CREATE TABLE proceedings (
  conference_key varchar(100) NOT NULL,
  year int(11),
  cname varchar(200),
  conference text,
  pdf varchar(200),
  CONSTRAINT proceedings_pk PRIMARY KEY (conference_key)
);

CREATE TABLE awards (
  award_key varchar(100) NOT NULL,
  title text,
  abstract text,
  begindate date,
  enddate date,
  amount float,
  directorate text,
  program_officer text,
  CONSTRAINT awards_pk PRIMARY KEY (award_key)
);

CREATE TABLE investigator (
  investigator_key varchar(100) NOT NULL,
  name varchar(200),
  institution varchar(200),
  CONSTRAINT investigator_pk PRIMARY KEY (investigator_key)
);

CREATE TABLE investigator_award (
  award_key varchar(100) NOT NULL,
  investigator_key varchar(100) NOT NULL,
  name varchar(200),
  institution varchar(200),
  CONSTRAINT investigator_award_pk PRIMARY KEY (award_key,investigator_key),
  CONSTRAINT investigator_award_investigator_fk FOREIGN KEY (investigator_key) REFERENCES investigator(investigator_key),
  CONSTRAINT investigator_award_award_fk FOREIGN KEY (award_key) REFERENCES awards(award_key)
);

CREATE TABLE award_publication (
  award_key varchar(100) NOT NULL,
  paper_key varchar(200) NOT NULL,
  CONSTRAINT award_publication_pk PRIMARY KEY (award_key,investigator_key),
  CONSTRAINT award_publication_paper_fk FOREIGN KEY (paper_key) REFERENCES paper(paper_key),
  CONSTRAINT iaward_publication_award_fk FOREIGN KEY (award_key) REFERENCES awards(award_key)
);