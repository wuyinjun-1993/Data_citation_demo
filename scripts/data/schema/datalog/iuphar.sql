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
    CONSTRAINT family_pk PRIMARY KEY (family_id),
    CONSTRAINT family_introduction_fk FOREIGN KEY (family_id) REFERENCES introduction(family_id)
);

CREATE TABLE introduction (
    family_id integer NOT NULL,
    text text NOT NULL,
    last_modified date,
    annotation_status integer DEFAULT 5 NOT NULL,
    no_contributor_list boolean DEFAULT true NOT NULL,
    intro_vector tsvector,
    CONSTRAINT family_pk PRIMARY KEY (family_id),
    CONSTRAINT ligand_introduction_fk FOREIGN KEY (family_id) REFERENCES fakeLigand(ligand_id)
);

CREATE TABLE contributor (
    contributor_id integer NOT NULL,
    address text,
    email character varying(500),
    first_names character varying(500) NOT NULL,
    surname character varying(500) NOT NULL,
    suffix character varying(200),
    note character varying(1000),
    orcid character varying(100),
    country character varying(50),
    description character varying(1000),
    name_vector tsvector,
    CONSTRAINT contributor_pkey PRIMARY KEY (contributor_id)
);

CREATE TABLE contributor2family (
    contributor_id integer NOT NULL,
    family_id integer NOT NULL,
    role character varying(50),
    display_order integer,
    CONSTRAINT contributor2family_pk PRIMARY KEY (contributor_id, family_id),
    CONSTRAINT contributor_contributor2family_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id),
    CONSTRAINT family_contributor2family_fk FOREIGN KEY (family_id) REFERENCES family(family_id)
);