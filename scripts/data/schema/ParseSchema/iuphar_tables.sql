    

CREATE TABLE accessory_protein (
    object_id integer NOT NULL,
    full_name character varying(1000)
);


CREATE TABLE allele (
    allele_id integer NOT NULL,
    accessions character varying(100) NOT NULL,
    species_id integer DEFAULT 2 NOT NULL,
    pubmed_ids character varying(200),
    ontology_id integer DEFAULT 1 NOT NULL,
    term_id character varying(100) NOT NULL,
    allelic_composition character varying(300),
    allele_symbol character varying(300),
    genetic_background character varying(300)
);


CREATE TABLE altered_expression (
    altered_expression_id integer NOT NULL,
    object_id integer NOT NULL,
    description character varying(10000),
    species_id integer NOT NULL,
    tissue character varying(1000),
    technique character varying(500),
    description_vector tsvector,
    tissue_vector tsvector,
    technique_vector tsvector
);
CREATE TABLE altered_expression_refs (
    altered_expression_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE analogue_cluster (
    ligand_id integer NOT NULL,
    cluster character varying(10) NOT NULL
);


CREATE TABLE associated_protein (
    associated_protein_id integer NOT NULL,
    object_id integer NOT NULL,
    name character varying(1000),
    type character varying(200) NOT NULL,
    associated_object_id integer,
    effect character varying(1000),
    name_vector tsvector
);
CREATE TABLE associated_protein_refs (
    associated_protein_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE binding_partner (
    binding_partner_id integer NOT NULL,
    object_id integer NOT NULL,
    name character varying(300) NOT NULL,
    interaction character varying(200),
    effect character varying(2000),
    partner_object_id integer,
    name_vector tsvector,
    effect_vector tsvector,
    interaction_vector tsvector
);
CREATE TABLE binding_partner_refs (
    binding_partner_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE catalytic_receptor (
    object_id integer NOT NULL,
    rtk_class character varying(20)
);
CREATE TABLE celltype_assoc (
    celltype_assoc_id integer NOT NULL,
    object_id integer NOT NULL,
    immuno_celltype_id integer NOT NULL,
    comment character varying(500),
    comment_vector tsvector
);
CREATE TABLE celltype_assoc_colist (
    celltype_assoc_id integer NOT NULL,
    co_celltype_id integer NOT NULL
);
CREATE TABLE celltype_assoc_refs (
    celltype_assoc_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE cellular_location (
    cellular_location_id integer NOT NULL,
    object_id integer NOT NULL,
    location character varying(500),
    technique character varying(500),
    comments character varying(1000)
);
CREATE TABLE cellular_location_refs (
    cellular_location_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE chembl_cluster (
    object_id integer NOT NULL,
    chembl_id character varying(50) NOT NULL,
    cluster character varying(10) NOT NULL,
    cluster_family character varying(10) NOT NULL
);

CREATE TABLE co_celltype (
    co_celltype_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    definition character varying(1500) NOT NULL,
    last_modified date,
    type character varying(50) NOT NULL,
    name_vector tsvector,
    definition_vector tsvector,
    cellonto_id character varying(50) NOT NULL,
    cellonto_id_vector tsvector
);
CREATE TABLE co_celltype_isa (
    parent_id integer NOT NULL,
    child_id integer NOT NULL
);


CREATE TABLE co_celltype_relationship (
    co_celltype_rel_id integer NOT NULL,
    co_celltype_id integer NOT NULL,
    relationship_id character varying(50) NOT NULL,
    type character varying(100)
);


CREATE TABLE cofactor (
    cofactor_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer NOT NULL,
    ligand_id integer,
    name character varying(1000),
    comments character varying(1000),
    in_iuphar boolean DEFAULT true NOT NULL,
    in_grac boolean DEFAULT false NOT NULL,
    name_vector tsvector
);
CREATE TABLE cofactor_refs (
    cofactor_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE committee (
    committee_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    description character varying(2000),
    family_id integer
);

CREATE TABLE conductance (
    conductance_id integer NOT NULL,
    object_id integer NOT NULL,
    overall_channel_conductance character varying(500),
    macroscopic_current_rectification character varying(100),
    single_channel_current_rectification character varying(100),
    species_id integer NOT NULL
);
CREATE TABLE conductance_refs (
    conductance_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE conductance_states (
    conductance_states_id integer NOT NULL,
    object_id integer NOT NULL,
    receptor character varying(100) NOT NULL,
    state1_high double precision,
    state1_low double precision,
    state2_high double precision,
    state2_low double precision,
    state3_high double precision,
    state3_low double precision,
    state4_high double precision,
    state4_low double precision,
    state5_high double precision,
    state5_low double precision,
    state6_high double precision,
    state6_low double precision,
    most_frequent_state character varying(50)
);
CREATE TABLE conductance_states_refs (
    conductance_states_id integer NOT NULL,
    reference_id integer NOT NULL
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
    name_vector tsvector
);
CREATE TABLE contributor2committee (
    contributor_id integer NOT NULL,
    committee_id integer NOT NULL,
    role character varying(200),
    display_order integer
);
CREATE TABLE contributor2family (
    contributor_id integer NOT NULL,
    family_id integer NOT NULL,
    role character varying(50),
    display_order integer
);

CREATE TABLE contributor2intro (
    contributor_id integer NOT NULL,
    family_id integer NOT NULL,
    display_order integer NOT NULL
);

CREATE TABLE contributor2ligand (
    ligand_id integer,
    first_names text,
    surname text
);

CREATE TABLE contributor2object (
    contributor_id integer NOT NULL,
    object_id integer NOT NULL,
    display_order integer
);

CREATE TABLE contributor_link (
    contributor_id integer NOT NULL,
    url character varying(500) NOT NULL
);


CREATE TABLE coregulator (
    coregulator_id integer NOT NULL,
    object_id integer NOT NULL,
    activity character varying(500),
    specific boolean,
    ligand_dependent boolean DEFAULT false NOT NULL,
    af2_dependent boolean,
    comments character varying(2000),
    coregulator_gene_id integer,
    activity_vector tsvector,
    comments_vector tsvector
);


CREATE TABLE coregulator_gene (
    coregulator_gene_id integer NOT NULL,
    primary_name character varying(300) NOT NULL,
    official_gene_id character varying(100),
    other_names character varying(1000),
    species_id integer NOT NULL,
    nursa_id character varying(100),
    comments character varying(2000),
    gene_long_name character varying(2000),
    primary_name_vector tsvector,
    other_names_vector tsvector,
    comments_vector tsvector,
    gene_long_name_vector tsvector
);
CREATE TABLE coregulator_refs (
    coregulator_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE database (
    database_id integer NOT NULL,
    name character varying(100) NOT NULL,
    url text,
    specialist boolean DEFAULT false NOT NULL,
    prefix character varying(100)
);


CREATE TABLE database_link (
    database_link_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer DEFAULT 9 NOT NULL,
    database_id integer NOT NULL,
    placeholder character varying(100) NOT NULL
);
CREATE TABLE deleted_family (
    family_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    previous_names character varying(300),
    type character varying(25) NOT NULL,
    old_family_id integer,
    new_family_id integer
);
CREATE TABLE discoverx (
    cat_no character varying(100) NOT NULL,
    url character varying(500) NOT NULL,
    name character varying(500) NOT NULL,
    description character varying(1000) NOT NULL,
    species_id integer NOT NULL
);


CREATE TABLE disease (
    disease_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    description text,
    name_vector tsvector,
    description_vector tsvector
);


CREATE TABLE disease2synonym (
    disease2synonym_id integer NOT NULL,
    disease_id integer NOT NULL,
    synonym character varying(1000) NOT NULL,
    synonym_vector tsvector
);


CREATE TABLE disease_database_link (
    disease_database_link_id integer NOT NULL,
    disease_id integer NOT NULL,
    database_id integer NOT NULL,
    placeholder character varying(100) NOT NULL
);
CREATE TABLE disease_synonym2database_link (
    disease2synonym_id integer NOT NULL,
    disease_database_link_id integer NOT NULL
);


CREATE TABLE dna_binding (
    dna_binding_id integer NOT NULL,
    object_id integer NOT NULL,
    structure character varying(500),
    sequence character varying(100),
    response_element character varying(500),
    structure_vector tsvector,
    sequence_vector tsvector,
    response_element_vector tsvector
);
CREATE TABLE dna_binding_refs (
    dna_binding_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE drug2disease (
    ligand_id integer NOT NULL,
    disease_id integer NOT NULL
);
CREATE TABLE enzyme (
    object_id integer NOT NULL
);


CREATE TABLE expression_experiment (
    expression_experiment_id integer NOT NULL,
    description character varying(1000),
    technique character varying(100),
    species_id integer NOT NULL,
    baseline double precision NOT NULL
);
CREATE TABLE expression_level (
    structural_info_id integer NOT NULL,
    tissue_id integer NOT NULL,
    expression_experiment_id integer NOT NULL,
    value double precision NOT NULL
);


CREATE TABLE expression_pathophysiology (
    expression_pathophysiology_id integer NOT NULL,
    object_id integer NOT NULL,
    change text,
    pathophysiology text,
    species_id integer NOT NULL,
    tissue character varying(1000),
    technique character varying(500),
    change_vector tsvector,
    tissue_vector tsvector,
    pathophysiology_vector tsvector,
    technique_vector tsvector
);
CREATE TABLE expression_pathophysiology_refs (
    expression_pathophysiology_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE family (
    family_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    last_modified date,
    old_family_id integer,
    type character varying(50) NOT NULL,
    display_order integer,
    annotation_status integer DEFAULT 5 NOT NULL,
    previous_names character varying(300),
    only_grac boolean,
    only_iuphar boolean,
    in_cgtp boolean DEFAULT true NOT NULL,
    name_vector tsvector,
    previous_names_vector tsvector
);

CREATE TABLE functional_assay (
    functional_assay_id integer NOT NULL,
    object_id integer NOT NULL,
    description character varying(1000) NOT NULL,
    response_measured character varying(1000) NOT NULL,
    species_id integer NOT NULL,
    tissue character varying(1000) NOT NULL,
    description_vector tsvector,
    tissue_vector tsvector,
    response_vector tsvector
);
CREATE TABLE functional_assay_refs (
    functional_assay_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE further_reading (
    object_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE go_process (
    go_process_id integer NOT NULL,
    term character varying(1000) NOT NULL,
    definition character varying(1500) NOT NULL,
    last_modified date,
    annotation character varying(200) NOT NULL,
    term_vector tsvector,
    definition_vector tsvector,
    go_id character varying(50) NOT NULL,
    go_id_vector tsvector
);
CREATE TABLE go_process_rel (
    parent_id integer NOT NULL,
    child_id integer NOT NULL
);
CREATE TABLE gpcr (
    object_id integer NOT NULL,
    class character varying(200),
    ligand character varying(500)
);

CREATE TABLE grac_family_text (
    family_id integer NOT NULL,
    overview text,
    comments text,
    last_modified date,
    overview_vector tsvector,
    comments_vector tsvector
);
CREATE TABLE grac_functional_characteristics (
    object_id integer NOT NULL,
    functional_characteristics text NOT NULL,
    functional_characteristics_vector tsvector
);
CREATE TABLE grac_further_reading (
    family_id integer NOT NULL,
    reference_id integer NOT NULL,
    key_ref boolean DEFAULT false NOT NULL
);


CREATE TABLE grac_ligand_rank_potency (
    grac_ligand_rank_potency_id integer NOT NULL,
    object_id integer NOT NULL,
    description character varying(500) NOT NULL,
    rank_potency character varying(2000) NOT NULL,
    species_id integer NOT NULL,
    in_iuphar boolean DEFAULT true NOT NULL,
    rank_potency_vector tsvector
);
CREATE TABLE grac_ligand_rank_potency_refs (
    grac_ligand_rank_potency_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE grac_transduction (
    object_id integer NOT NULL,
    transduction character varying(1000) NOT NULL
);
CREATE TABLE "grouping" (
    group_id integer NOT NULL,
    family_id integer NOT NULL,
    display_order integer DEFAULT 1 NOT NULL
);
CREATE TABLE gtip2go_process (
    gtip_process_id integer NOT NULL,
    go_process_id integer NOT NULL,
    comment character varying(500)
);


CREATE TABLE gtip_process (
    gtip_process_id integer NOT NULL,
    term character varying(1000) NOT NULL,
    definition character varying(1500) NOT NULL,
    last_modified date,
    term_vector tsvector
);

CREATE TABLE immuno2co_celltype (
    immuno_celltype_id integer NOT NULL,
    cellonto_id character varying(50) NOT NULL,
    comment character varying(500)
);


CREATE TABLE immuno_celltype (
    immuno_celltype_id integer NOT NULL,
    term character varying(1000) NOT NULL,
    definition character varying(1500) NOT NULL,
    last_modified date,
    term_vector tsvector
);


CREATE TABLE immuno_disease2ligand (
    immuno_disease2ligand_id integer NOT NULL,
    ligand_id integer NOT NULL,
    disease_id integer,
    comment character varying(500),
    comment_vector tsvector
);
CREATE TABLE immuno_disease2ligand_refs (
    immuno_disease2ligand_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE immuno_disease2object (
    immuno_disease2object_id integer NOT NULL,
    object_id integer NOT NULL,
    disease_id integer,
    comment character varying(500),
    comment_vector tsvector
);
CREATE TABLE immuno_disease2object_refs (
    immuno_disease2object_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE inn (
    inn_number integer NOT NULL,
    inn character varying(500) NOT NULL,
    cas character varying(100),
    smiles text,
    smiles_salts_stripped text,
    inchi_key_salts_stripped character varying(500),
    nonisomeric_smiles_salts_stripped text,
    nonisomeric_inchi_key_salts_stripped character varying(500),
    neutralised_smiles text,
    neutralised_inchi_key character varying(500),
    neutralised_nonisomeric_smiles text,
    neutralised_nonisomeric_inchi_key character varying(500),
    inn_vector tsvector
);


CREATE TABLE interaction (
    interaction_id integer NOT NULL,
    ligand_id integer NOT NULL,
    object_id integer,
    type character varying(100) NOT NULL,
    action character varying(1000) NOT NULL,
    action_comment character varying(2000),
    species_id integer NOT NULL,
    endogenous boolean DEFAULT false NOT NULL,
    selective boolean DEFAULT false NOT NULL,
    use_dependent boolean,
    voltage_dependent boolean,
    affinity_units character varying(100),
    affinity_high double precision,
    affinity_median double precision,
    affinity_low double precision,
    concentration_range character varying(200),
    affinity_voltage_high real,
    affinity_voltage_median real,
    affinity_voltage_low real,
    affinity_physiological_voltage boolean,
    rank integer,
    selectivity character varying(100),
    original_affinity_low_nm double precision,
    original_affinity_median_nm double precision,
    original_affinity_high_nm double precision,
    original_affinity_units character varying(20),
    original_affinity_relation character varying(10),
    assay_description character varying(1000),
    assay_conditions character varying(1000),
    from_grac boolean DEFAULT false NOT NULL,
    only_grac boolean DEFAULT false NOT NULL,
    receptor_site character varying(300),
    ligand_context character varying(300),
    percent_activity double precision,
    assay_url character varying(500),
    primary_target boolean,
    target_ligand_id integer,
    type_vector tsvector
);
CREATE TABLE interaction_affinity_refs (
    interaction_id integer NOT NULL,
    reference_id integer NOT NULL
);

CREATE TABLE introduction (
    family_id integer NOT NULL,
    text text NOT NULL,
    last_modified date,
    annotation_status integer DEFAULT 5 NOT NULL,
    no_contributor_list boolean DEFAULT true NOT NULL,
    intro_vector tsvector
);

CREATE TABLE iuphar2discoverx (
    object_id integer NOT NULL,
    cat_no character varying(100) NOT NULL
);
CREATE TABLE lgic (
    object_id integer NOT NULL,
    ligand character varying(500),
    selectivity_comments text
);


CREATE TABLE ligand (
    ligand_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    pubchem_sid bigint,
    radioactive boolean DEFAULT false NOT NULL,
    old_ligand_id integer,
    type character varying(50) NOT NULL,
    approved boolean,
    approved_source character varying(100),
    iupac_name character varying(1000),
    comments character varying(2000),
    withdrawn_drug boolean,
    verified boolean,
    abbreviation character varying(300),
    clinical_use text,
    mechanism_of_action text,
    absorption_distribution text,
    metabolism text,
    elimination text,
    popn_pharmacokinetics text,
    organ_function_impairment text,
    emc_url character varying(1000),
    drugs_url character varying(1000),
    ema_url character varying(1000),
    bioactivity_comments text,
    labelled boolean,
    in_gtip boolean,
    immuno_comments text,
    name_vector tsvector,
    comments_vector tsvector,
    abbreviation_vector tsvector,
    clinical_use_vector tsvector,
    mechanism_of_action_vector tsvector,
    absorption_distribution_vector tsvector,
    metabolism_vector tsvector,
    elimination_vector tsvector,
    popn_pharmacokinetics_vector tsvector,
    organ_function_impairment_vector tsvector,
    bioactivity_comments_vector tsvector
);
CREATE TABLE ligand2inn (
    ligand_id integer NOT NULL,
    inn_number integer NOT NULL
);
CREATE TABLE ligand2meshpharmacology (
    ligand_id integer NOT NULL,
    mesh_term character varying(1000) NOT NULL,
    type character varying(100) NOT NULL
);
CREATE TABLE ligand2subunit (
    ligand_id integer NOT NULL,
    subunit_id integer NOT NULL
);


CREATE TABLE ligand2synonym (
    ligand_id integer NOT NULL,
    synonym character varying(2000) NOT NULL,
    from_grac boolean DEFAULT false NOT NULL,
    ligand2synonym_id integer NOT NULL,
    display boolean DEFAULT true NOT NULL,
    synonym_vector tsvector
);
CREATE TABLE ligand2synonym_refs (
    ligand2synonym_id integer NOT NULL,
    reference_id integer NOT NULL
);

CREATE TABLE ligand_cluster (
    ligand_id integer NOT NULL,
    cluster character varying(100) NOT NULL,
    distance double precision NOT NULL,
    cluster_centre integer NOT NULL
);


CREATE TABLE ligand_database_link (
    ligand_database_link_id integer NOT NULL,
    ligand_id integer NOT NULL,
    database_id integer NOT NULL,
    placeholder character varying(100) NOT NULL,
    source character varying(100),
    commercial boolean DEFAULT false,
    species_id integer DEFAULT 9 NOT NULL
);
CREATE TABLE ligand_physchem (
    ligand_id integer NOT NULL,
    hydrogen_bond_acceptors integer NOT NULL,
    hydrogen_bond_donors integer NOT NULL,
    rotatable_bonds_count integer NOT NULL,
    topological_polar_surface_area double precision NOT NULL,
    molecular_weight double precision NOT NULL,
    xlogp double precision NOT NULL,
    lipinski_s_rule_of_five integer NOT NULL
);
CREATE TABLE ligand_structure (
    ligand_id integer NOT NULL,
    isomeric_smiles text NOT NULL,
    isomeric_standard_inchi text,
    isomeric_standard_inchi_key character varying(300) NOT NULL,
    nonisomeric_smiles text NOT NULL,
    nonisomeric_standard_inchi text,
    nonisomeric_standard_inchi_key character varying(300) NOT NULL
);
CREATE TABLE list_ligand (
    object_id integer NOT NULL,
    ligand_id integer NOT NULL,
    display_order integer DEFAULT 0 NOT NULL
);
CREATE TABLE multimer (
    object_id integer NOT NULL,
    subunit_specific_agents_comments text
);


CREATE TABLE mutation (
    mutation_id integer NOT NULL,
    pathophysiology_id integer,
    object_id integer NOT NULL,
    type character varying(100) NOT NULL,
    amino_acid_change character varying(100),
    species_id integer NOT NULL,
    description character varying(1000),
    nucleotide_change character varying(100)
);
CREATE TABLE mutation_refs (
    mutation_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE nhr (
    object_id integer NOT NULL,
    ligand character varying(500),
    binding_partner_comments text,
    coregulator_comments text,
    dna_binding_comments text,
    target_gene_comments text
);


CREATE TABLE object (
    object_id integer NOT NULL,
    name character varying(1000) NOT NULL,
    last_modified date,
    comments text,
    structural_info_comments text,
    old_object_id integer,
    annotation_status integer DEFAULT 5 NOT NULL,
    only_iuphar boolean,
    grac_comments text,
    only_grac boolean,
    no_contributor_list boolean DEFAULT true NOT NULL,
    abbreviation character varying(100),
    systematic_name character varying(100),
    quaternary_structure_comments text,
    in_cgtp boolean DEFAULT true NOT NULL,
    in_gtip boolean DEFAULT false,
    gtip_comment text
);

CREATE TABLE object2go_process (
    object_id integer NOT NULL,
    go_process_id integer NOT NULL,
    go_evidence character varying(5),
    comment character varying(500)
);
CREATE TABLE object2reaction (
    object_id integer NOT NULL,
    reaction_id integer NOT NULL
);

CREATE TABLE object_vectors (
    object_id integer NOT NULL,
    name tsvector,
    abbreviation tsvector,
    comments tsvector,
    grac_comments tsvector,
    structural_info_comments tsvector,
    associated_proteins_comments tsvector,
    functional_assay_comments tsvector,
    tissue_distribution_comments tsvector,
    functions_comments tsvector,
    altered_expression_comments tsvector,
    expression_pathophysiology_comments tsvector,
    mutations_pathophysiology_comments tsvector,
    variants_comments tsvector,
    xenobiotic_expression_comments tsvector,
    antibody_comments tsvector,
    agonists_comments tsvector,
    antagonists_comments tsvector,
    allosteric_modulators_comments tsvector,
    activators_comments tsvector,
    inhibitors_comments tsvector,
    channel_blockers_comments tsvector,
    gating_inhibitors_comments tsvector,
    subunit_specific_agents_comments tsvector,
    selectivity_comments tsvector,
    voltage_dependence_comments tsvector,
    target_gene_comments tsvector,
    dna_binding_comments tsvector,
    coregulator_comments tsvector,
    binding_partner_comments tsvector
);


CREATE TABLE ontology (
    ontology_id integer NOT NULL,
    name character varying(100) NOT NULL,
    short_name character varying(100)
);
CREATE TABLE ontology_term (
    ontology_id integer DEFAULT 1 NOT NULL,
    term_id character varying(100) NOT NULL,
    term character varying(1000),
    description character varying(3000)
);
CREATE TABLE other_ic (
    object_id integer NOT NULL,
    selectivity_comments text
);
CREATE TABLE other_protein (
    object_id integer NOT NULL
);


CREATE TABLE pathophysiology (
    pathophysiology_id integer NOT NULL,
    object_id integer NOT NULL,
    disease character varying(2000),
    role character varying(2000),
    drugs character varying(2000),
    side_effects character varying(2000),
    use character varying(2000),
    omim character varying(200),
    comments text,
    orphanet character varying(200),
    disease_id integer,
    role_vector tsvector,
    drugs_vector tsvector,
    side_effects_vector tsvector,
    use_vector tsvector,
    comments_vector tsvector
);

CREATE TABLE pathophysiology_refs (
    pathophysiology_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE pdb_structure (
    pdb_structure_id integer NOT NULL,
    object_id integer NOT NULL,
    ligand_id integer,
    endogenous boolean DEFAULT false NOT NULL,
    pdb_code character varying(4),
    description character varying(1000),
    resolution double precision,
    species_id integer NOT NULL,
    description_vector tsvector
);
CREATE TABLE pdb_structure_refs (
    pdb_structure_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE peptide (
    ligand_id integer NOT NULL,
    one_letter_seq text,
    three_letter_seq text,
    post_translational_modifications character varying(1000),
    chemical_modifications character varying(1000),
    medical_relevance character varying(2000)
);
CREATE TABLE peptide_ligand_cluster (
    ligand_id integer NOT NULL,
    cluster character varying(10)
);
CREATE TABLE peptide_ligand_sequence_cluster (
    ligand_id integer NOT NULL,
    cluster integer NOT NULL
);


CREATE TABLE physiological_function (
    physiological_function_id integer NOT NULL,
    object_id integer NOT NULL,
    description text NOT NULL,
    species_id integer NOT NULL,
    tissue text NOT NULL,
    description_vector tsvector,
    tissue_vector tsvector
);
CREATE TABLE physiological_function_refs (
    physiological_function_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE precursor (
    precursor_id integer NOT NULL,
    gene_name character varying(100),
    official_gene_id character varying(100),
    protein_name character varying(200),
    species_id integer NOT NULL,
    gene_long_name character varying(2000),
    protein_name_vector tsvector,
    gene_long_name_vector tsvector
);
CREATE TABLE precursor2peptide (
    precursor_id integer NOT NULL,
    ligand_id integer NOT NULL
);


CREATE TABLE precursor2synonym (
    precursor2synonym_id integer NOT NULL,
    precursor_id integer NOT NULL,
    synonym character varying(2000) NOT NULL,
    synonym_vector tsvector
);


CREATE TABLE primary_regulator (
    primary_regulator_id integer NOT NULL,
    object_id integer NOT NULL,
    name character varying(1000),
    regulatory_effect character varying(2000),
    regulator_object_id integer
);
CREATE TABLE primary_regulator_refs (
    primary_regulator_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE process_assoc (
    object_id integer NOT NULL,
    gtip_process_id integer NOT NULL,
    comment character varying(500),
    direct_annotation boolean DEFAULT false NOT NULL,
    go_annotation integer DEFAULT 0 NOT NULL,
    process_assoc_id integer NOT NULL,
    comment_vector tsvector
);

CREATE TABLE process_assoc_refs (
    process_assoc_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE prodrug (
    prodrug_ligand_id integer NOT NULL,
    drug_ligand_id integer NOT NULL
);


CREATE TABLE product (
    product_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer NOT NULL,
    ligand_id integer,
    name character varying(1000),
    endogenous boolean DEFAULT true NOT NULL,
    in_iuphar boolean DEFAULT true NOT NULL,
    in_grac boolean DEFAULT false NOT NULL,
    name_vector tsvector
);
CREATE TABLE product_refs (
    product_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE query2conditions (
    query_id text,
    conditions text
);
CREATE TABLE query2head_variables (
    query_id text,
    head_variables text,
    name text
);
CREATE TABLE query2lambda_term (
    query_id text,
    lambda_term text,
    table_name text
);
CREATE TABLE query2subgoal (
    query_id text,
    subgoal_names text,
    subgoal_origin_names text
);
CREATE TABLE query_table (
    query_id integer NOT NULL,
    query text,
    suffix text,
    condition text
);

CREATE TABLE reaction (
    reaction_id integer NOT NULL,
    ec_number character varying(50) NOT NULL,
    reaction character varying(3000)
);

CREATE TABLE receptor2family (
    object_id integer NOT NULL,
    family_id integer NOT NULL,
    display_order integer NOT NULL
);
CREATE TABLE receptor2subunit (
    receptor_id integer NOT NULL,
    subunit_id integer NOT NULL,
    type character varying(200)
);
CREATE TABLE receptor_basic (
    object_id integer NOT NULL,
    list_comments character varying(1000),
    associated_proteins_comments text,
    functional_assay_comments text,
    tissue_distribution_comments text,
    functions_comments text,
    altered_expression_comments text,
    expression_pathophysiology_comments text,
    mutations_pathophysiology_comments text,
    variants_comments text,
    xenobiotic_expression_comments text,
    antibody_comments text,
    agonists_comments text,
    antagonists_comments text,
    allosteric_modulators_comments text,
    activators_comments text,
    inhibitors_comments text,
    channel_blockers_comments text,
    gating_inhibitors_comments text
);


CREATE TABLE reference (
    reference_id integer NOT NULL,
    type character varying(50) NOT NULL,
    title character varying(2000),
    article_title character varying(1000) NOT NULL,
    year smallint,
    issue character varying(50),
    volume character varying(50),
    pages character varying(50),
    publisher character varying(500),
    publisher_address character varying(2000),
    editors character varying(2000),
    pubmed_id bigint,
    isbn character varying(13),
    pub_status character varying(100),
    topics character varying(250),
    comments character varying(500),
    read boolean,
    useful boolean,
    website character varying(500),
    url character varying(2000),
    doi character varying(500),
    accessed date,
    modified date,
    patent_number character varying(250),
    priority date,
    publication date,
    authors text,
    assignee character varying(500),
    authors_vector tsvector,
    article_title_vector tsvector
);
CREATE TABLE reference2ligand (
    reference_id integer NOT NULL,
    ligand_id integer NOT NULL
);


CREATE TABLE screen (
    screen_id integer NOT NULL,
    name character varying(500) NOT NULL,
    description text,
    url character varying(1000),
    affinity_cut_off_nm integer,
    company_logo_filename character varying(250),
    technology_logo_filename character varying(250)
);


CREATE TABLE screen_interaction (
    screen_interaction_id integer NOT NULL,
    screen_id integer NOT NULL,
    ligand_id integer NOT NULL,
    object_id integer NOT NULL,
    type character varying(100) NOT NULL,
    action character varying(1000) NOT NULL,
    action_comment character varying(2000) NOT NULL,
    species_id integer NOT NULL,
    endogenous boolean DEFAULT false NOT NULL,
    affinity_units character varying(100) NOT NULL,
    affinity_high double precision,
    affinity_median double precision,
    affinity_low double precision,
    concentration_range character varying(200),
    original_affinity_low_nm double precision,
    original_affinity_median_nm double precision,
    original_affinity_high_nm double precision,
    original_affinity_units character varying(20),
    original_affinity_relation character varying(10),
    assay_description character varying(1000),
    percent_activity double precision,
    assay_url character varying(500)
);
CREATE TABLE screen_refs (
    screen_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE selectivity (
    selectivity_id integer NOT NULL,
    object_id integer NOT NULL,
    ion character varying(20) NOT NULL,
    conductance_high real,
    conductance_low real,
    conductance_median real,
    hide_conductance boolean,
    species_id integer NOT NULL
);
CREATE TABLE selectivity_refs (
    selectivity_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE species (
    species_id integer UNIQUE NOT NULL,
    name character varying(100) UNIQUE NOT NULL,
    short_name character varying(6) NOT NULL,
    scientific_name character varying(200),
    ncbi_taxonomy_id integer
);


CREATE TABLE specific_reaction (
    specific_reaction_id integer NOT NULL,
    object_id integer NOT NULL,
    reaction_id integer NOT NULL,
    description character varying(1000),
    reaction character varying(3000) NOT NULL
);
CREATE TABLE specific_reaction_refs (
    specific_reaction_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE structural_info (
    structural_info_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer NOT NULL,
    transmembrane_domains integer,
    amino_acids integer,
    pore_loops integer,
    genomic_location character varying(50),
    gene_name character varying(100),
    official_gene_id character varying(100),
    molecular_weight integer,
    gene_long_name character varying(2000),
    gene_long_name_vector tsvector
);
CREATE TABLE structural_info_refs (
    structural_info_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE subcommittee (
    contributor_id integer NOT NULL,
    family_id integer NOT NULL,
    role character varying(50),
    display_order integer NOT NULL
);


CREATE TABLE substrate (
    substrate_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer NOT NULL,
    ligand_id integer,
    property character varying(20) NOT NULL,
    value double precision,
    units character varying(100),
    assay_description character varying(1000),
    assay_conditions character varying(1000),
    comments character varying(1000),
    name character varying(1000),
    endogenous boolean DEFAULT true NOT NULL,
    in_iuphar boolean DEFAULT true NOT NULL,
    in_grac boolean DEFAULT false NOT NULL,
    standard_property character varying(100),
    standard_value double precision,
    name_vector tsvector
);
CREATE TABLE substrate_refs (
    substrate_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE synonym (
    synonym_id integer NOT NULL,
    object_id integer NOT NULL,
    synonym character varying(2000) NOT NULL,
    display boolean DEFAULT true NOT NULL,
    from_grac boolean DEFAULT false NOT NULL,
    synonym_vector tsvector
);
CREATE TABLE synonym_refs (
    synonym_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE target_gene (
    target_gene_id integer NOT NULL,
    object_id integer NOT NULL,
    species_id integer NOT NULL,
    description character varying(1000),
    official_gene_id character varying(100),
    effect character varying(300),
    technique character varying(500),
    comments character varying(2000),
    description_vector tsvector,
    effect_vector tsvector,
    technique_vector tsvector,
    comments_vector tsvector
);
CREATE TABLE target_gene_refs (
    target_gene_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE target_ligand_same_entity (
    object_id integer NOT NULL,
    ligand_id integer NOT NULL
);


CREATE TABLE tissue (
    tissue_id integer NOT NULL,
    name character varying(100) NOT NULL
);


CREATE TABLE tissue_distribution (
    tissue_distribution_id integer NOT NULL,
    object_id integer NOT NULL,
    tissues character varying(10000) NOT NULL,
    species_id integer NOT NULL,
    technique character varying(1000),
    expression_level integer,
    tissues_vector tsvector,
    technique_vector tsvector
);
CREATE TABLE tissue_distribution_refs (
    tissue_distribution_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE transduction (
    transduction_id integer NOT NULL,
    object_id integer NOT NULL,
    secondary boolean NOT NULL,
    t01 boolean DEFAULT false NOT NULL,
    t02 boolean DEFAULT false NOT NULL,
    t03 boolean DEFAULT false NOT NULL,
    t04 boolean DEFAULT false NOT NULL,
    t05 boolean DEFAULT false NOT NULL,
    t06 boolean DEFAULT false NOT NULL,
    e01 boolean DEFAULT false NOT NULL,
    e02 boolean DEFAULT false NOT NULL,
    e03 boolean DEFAULT false NOT NULL,
    e04 boolean DEFAULT false NOT NULL,
    e05 boolean DEFAULT false NOT NULL,
    e06 boolean DEFAULT false NOT NULL,
    e07 boolean DEFAULT false NOT NULL,
    e08 boolean DEFAULT false NOT NULL,
    e09 boolean DEFAULT false NOT NULL,
    comments character varying(10000),
    comments_vector tsvector
);
CREATE TABLE transduction_refs (
    transduction_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE transporter (
    object_id integer NOT NULL,
    grac_stoichiometry character varying(1000),
    grac_stoichiometry_vector tsvector
);


CREATE TABLE variant (
    variant_id integer NOT NULL,
    object_id integer NOT NULL,
    description character varying(2000),
    type character varying(100),
    species_id integer NOT NULL,
    amino_acids integer,
    amino_acid_change character varying(500),
    validation character varying(1000),
    global_maf character varying(100),
    subpop_maf character varying(1000),
    minor_allele_count character varying(500),
    frequency_comment character varying(1000),
    nucleotide_change character varying(500),
    description_vector tsvector
);
CREATE TABLE variant2database_link (
    variant_id integer NOT NULL,
    database_link_id integer NOT NULL,
    type character varying(50) NOT NULL
);
CREATE TABLE variant_refs (
    variant_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE version (
    version_number character varying(100) NOT NULL,
    publish_date date
);
CREATE TABLE vgic (
    object_id integer NOT NULL,
    physiological_ion character varying(100),
    selectivity_comments text,
    voltage_dependence_comments text
);

CREATE TABLE voltage_dep_activation_refs (
    voltage_dependence_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE voltage_dep_deactivation_refs (
    voltage_dependence_id integer NOT NULL,
    reference_id integer NOT NULL
);
CREATE TABLE voltage_dep_inactivation_refs (
    voltage_dependence_id integer NOT NULL,
    reference_id integer NOT NULL
);


CREATE TABLE voltage_dependence (
    voltage_dependence_id integer NOT NULL,
    object_id integer NOT NULL,
    cell_type character varying(500) NOT NULL,
    comments text,
    activation_v_high double precision,
    activation_v_median double precision,
    activation_v_low double precision,
    activation_t_high double precision,
    activation_t_low double precision,
    inactivation_v_high double precision,
    inactivation_v_median double precision,
    inactivation_v_low double precision,
    inactivation_t_high double precision,
    inactivation_t_low double precision,
    deactivation_v_high double precision,
    deactivation_v_median double precision,
    deactivation_v_low double precision,
    deactivation_t_high double precision,
    deactivation_t_low double precision,
    species_id integer NOT NULL,
    cell_type_vector tsvector,
    comments_vector tsvector
);

CREATE TABLE xenobiotic_expression (
    xenobiotic_expression_id integer NOT NULL,
    object_id integer NOT NULL,
    change character varying(2000),
    technique character varying(500),
    tissue character varying(1000),
    species_id integer NOT NULL,
    change_vector tsvector,
    tissue_vector tsvector,
    technique_vector tsvector
);

CREATE TABLE xenobiotic_expression_refs (
    xenobiotic_expression_id integer NOT NULL,
    reference_id integer NOT NULL
);
