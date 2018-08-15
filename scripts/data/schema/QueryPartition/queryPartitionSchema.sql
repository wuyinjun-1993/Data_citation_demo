CREATE TABLE family
(
    family_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    last_modified date,
    old_family_id integer,
    type character varying(50) NOT NULL,
    display_order integer,
    annotation_status integer NOT NULL,
    previous_names character varying(300),
    only_grac boolean,
    only_iuphar boolean,
    in_cgtp boolean NOT NULL,
    name_vector tsvector,
    previous_names_vector tsvector,
    CONSTRAINT family_pk PRIMARY KEY (family_id)
);
CREATE TABLE introduction (
    family_id integer NOT NULL,
    text text NOT NULL,
    last_modified date,
    annotation_status integer DEFAULT 5 NOT NULL,
    no_contributor_list boolean DEFAULT true NOT NULL,
    intro_vector tsvector,
    CONSTRAINT family_pk PRIMARY KEY (family_id),
    CONSTRAINT family_introduction_fk FOREIGN KEY (family_id) REFERENCES family(family_id)
);