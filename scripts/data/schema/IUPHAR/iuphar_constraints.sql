
ALTER TABLE target_gene_refs
    ADD CONSTRAINT target_gene_target_gene_refs_fk FOREIGN KEY (target_gene_id) REFERENCES target_gene(target_gene_id);


ALTER TABLE altered_expression_refs
    ADD CONSTRAINT altered_expression_refs_pk PRIMARY KEY (altered_expression_id, reference_id);


ALTER TABLE accessory_protein
    ADD CONSTRAINT accessory_protein_pk PRIMARY KEY (object_id);


ALTER TABLE allele
    ADD CONSTRAINT allele_pk PRIMARY KEY (allele_id);


ALTER TABLE altered_expression
    ADD CONSTRAINT altered_expression_pkey PRIMARY KEY (altered_expression_id);


ALTER TABLE analogue_cluster
    ADD CONSTRAINT analogue_cluster_pk PRIMARY KEY (ligand_id, cluster);


ALTER TABLE associated_protein
    ADD CONSTRAINT associated_protein_pk PRIMARY KEY (associated_protein_id);


ALTER TABLE associated_protein_refs
    ADD CONSTRAINT associated_protein_refs_pk PRIMARY KEY (associated_protein_id, reference_id);


ALTER TABLE binding_partner
    ADD CONSTRAINT binding_partner_pk PRIMARY KEY (binding_partner_id);


ALTER TABLE binding_partner_refs
    ADD CONSTRAINT binding_partner_refs_pk PRIMARY KEY (binding_partner_id, reference_id);


ALTER TABLE catalytic_receptor
    ADD CONSTRAINT catalytic_receptor_pk PRIMARY KEY (object_id);


ALTER TABLE celltype_assoc_colist
    ADD CONSTRAINT celltype_assoc_colist_pk PRIMARY KEY (celltype_assoc_id, co_celltype_id);


ALTER TABLE celltype_assoc
    ADD CONSTRAINT celltype_assoc_pk PRIMARY KEY (celltype_assoc_id);


ALTER TABLE celltype_assoc_refs
    ADD CONSTRAINT celltype_assoc_refs_pk PRIMARY KEY (celltype_assoc_id, reference_id);


ALTER TABLE cellular_location
    ADD CONSTRAINT cellular_location_pk PRIMARY KEY (cellular_location_id);


ALTER TABLE cellular_location_refs
    ADD CONSTRAINT cellular_location_refs_pk PRIMARY KEY (cellular_location_id, reference_id);


ALTER TABLE chembl_cluster
    ADD CONSTRAINT chembl_cluster_pk PRIMARY KEY (object_id, chembl_id);


ALTER TABLE co_celltype_isa
    ADD CONSTRAINT co_celltype_isa_pk PRIMARY KEY (parent_id, child_id);


ALTER TABLE co_celltype
    ADD CONSTRAINT co_celltype_pk PRIMARY KEY (co_celltype_id);


ALTER TABLE co_celltype_relationship
    ADD CONSTRAINT co_celltype_relationship_pk PRIMARY KEY (co_celltype_rel_id);


ALTER TABLE cofactor
    ADD CONSTRAINT cofactor_pk PRIMARY KEY (cofactor_id);


ALTER TABLE cofactor_refs
    ADD CONSTRAINT cofactor_refs_pk PRIMARY KEY (cofactor_id, reference_id);


ALTER TABLE committee
    ADD CONSTRAINT committee_pk PRIMARY KEY (committee_id);


ALTER TABLE conductance
    ADD CONSTRAINT conductance_pk PRIMARY KEY (conductance_id);


ALTER TABLE conductance_refs
    ADD CONSTRAINT conductance_refs_pk PRIMARY KEY (conductance_id, reference_id);


ALTER TABLE conductance_states
    ADD CONSTRAINT conductance_states_pk PRIMARY KEY (conductance_states_id);


ALTER TABLE conductance_states_refs
    ADD CONSTRAINT conductance_states_refs_pk PRIMARY KEY (conductance_states_id, reference_id);


ALTER TABLE contributor2committee
    ADD CONSTRAINT contributor2committee_pk PRIMARY KEY (contributor_id, committee_id);


ALTER TABLE contributor2family
    ADD CONSTRAINT contributor2family_pk PRIMARY KEY (contributor_id, family_id);


ALTER TABLE contributor2intro
    ADD CONSTRAINT contributor2intro_pk PRIMARY KEY (contributor_id, family_id);


ALTER TABLE contributor2object
    ADD CONSTRAINT contributor2object_pk PRIMARY KEY (contributor_id, object_id);


ALTER TABLE contributor_link
    ADD CONSTRAINT contributor_link_pk PRIMARY KEY (contributor_id, url);


ALTER TABLE contributor
    ADD CONSTRAINT contributor_pkey PRIMARY KEY (contributor_id);


ALTER TABLE coregulator_gene
    ADD CONSTRAINT coregulator_gene_pk PRIMARY KEY (coregulator_gene_id);


ALTER TABLE coregulator
    ADD CONSTRAINT coregulator_pk PRIMARY KEY (coregulator_id);


ALTER TABLE coregulator_refs
    ADD CONSTRAINT coregulator_refs_pk PRIMARY KEY (coregulator_id, reference_id);


ALTER TABLE database_link
    ADD CONSTRAINT database_link_pkey PRIMARY KEY (database_link_id);


ALTER TABLE database
    ADD CONSTRAINT database_pkey PRIMARY KEY (database_id);


ALTER TABLE deleted_family
    ADD CONSTRAINT deleted_family_pk PRIMARY KEY (family_id);


ALTER TABLE discoverx
    ADD CONSTRAINT discoverx_pk PRIMARY KEY (cat_no);


ALTER TABLE disease2synonym
    ADD CONSTRAINT disease2synonym_pk PRIMARY KEY (disease2synonym_id);


ALTER TABLE disease_database_link
    ADD CONSTRAINT disease_database_link_pk PRIMARY KEY (disease_database_link_id);


ALTER TABLE disease
    ADD CONSTRAINT disease_pk PRIMARY KEY (disease_id);


ALTER TABLE disease_synonym2database_link
    ADD CONSTRAINT disease_synonym2database_link_pk PRIMARY KEY (disease2synonym_id, disease_database_link_id);


ALTER TABLE dna_binding
    ADD CONSTRAINT dna_binding_pk PRIMARY KEY (dna_binding_id);


ALTER TABLE dna_binding_refs
    ADD CONSTRAINT dna_binding_refs_pk PRIMARY KEY (dna_binding_id, reference_id);


ALTER TABLE drug2disease
    ADD CONSTRAINT drug2disease_pk PRIMARY KEY (ligand_id, disease_id);


ALTER TABLE enzyme
    ADD CONSTRAINT enzyme_pk PRIMARY KEY (object_id);


ALTER TABLE expression_experiment
    ADD CONSTRAINT expression_experiment_pk PRIMARY KEY (expression_experiment_id);


ALTER TABLE expression_level
    ADD CONSTRAINT expression_level_pk PRIMARY KEY (structural_info_id, tissue_id, expression_experiment_id);


ALTER TABLE expression_pathophysiology
    ADD CONSTRAINT expression_pathophys_pk PRIMARY KEY (expression_pathophysiology_id);


ALTER TABLE expression_pathophysiology_refs
    ADD CONSTRAINT expression_pathophysiology_refs_pk PRIMARY KEY (expression_pathophysiology_id, reference_id);


ALTER TABLE family
    ADD CONSTRAINT family_pk PRIMARY KEY (family_id);


ALTER TABLE functional_assay
    ADD CONSTRAINT functional_assay_pkey PRIMARY KEY (functional_assay_id);


ALTER TABLE functional_assay_refs
    ADD CONSTRAINT functional_assay_refs_pk PRIMARY KEY (functional_assay_id, reference_id);


ALTER TABLE further_reading
    ADD CONSTRAINT further_reading_pk PRIMARY KEY (object_id, reference_id);


ALTER TABLE go_process
    ADD CONSTRAINT go_process_pk PRIMARY KEY (go_process_id);


ALTER TABLE go_process_rel
    ADD CONSTRAINT go_process_rel_pk PRIMARY KEY (parent_id, child_id);



ALTER TABLE gpcr
    ADD CONSTRAINT gpcr_pk PRIMARY KEY (object_id);


ALTER TABLE grac_family_text
    ADD CONSTRAINT grac_family_text_pk PRIMARY KEY (family_id);


ALTER TABLE grac_functional_characteristics
    ADD CONSTRAINT grac_functional_characteristics_pk PRIMARY KEY (object_id);


ALTER TABLE grac_further_reading
    ADD CONSTRAINT grac_further_reading_pk PRIMARY KEY (family_id, reference_id);


ALTER TABLE grac_ligand_rank_potency
    ADD CONSTRAINT grac_ligand_rank_potency_pk PRIMARY KEY (grac_ligand_rank_potency_id);


ALTER TABLE grac_ligand_rank_potency_refs
    ADD CONSTRAINT grac_ligand_rank_potency_refs_pk PRIMARY KEY (grac_ligand_rank_potency_id, reference_id);


ALTER TABLE grac_transduction
    ADD CONSTRAINT grac_transduction_pk PRIMARY KEY (object_id);


ALTER TABLE "grouping"
    ADD CONSTRAINT grouping_pk PRIMARY KEY (group_id, family_id);


ALTER TABLE gtip2go_process
    ADD CONSTRAINT gtip2go_process_pk PRIMARY KEY (gtip_process_id, go_process_id);



ALTER TABLE gtip_process
    ADD CONSTRAINT gtip_process_pk PRIMARY KEY (gtip_process_id);


ALTER TABLE immuno2co_celltype
    ADD CONSTRAINT immuno2co_celltype_pk PRIMARY KEY (immuno_celltype_id, cellonto_id);


ALTER TABLE immuno_celltype
    ADD CONSTRAINT immuno_celltype_pk PRIMARY KEY (immuno_celltype_id);


ALTER TABLE immuno_disease2ligand
    ADD CONSTRAINT immuno_disease2ligand_pk PRIMARY KEY (immuno_disease2ligand_id);


ALTER TABLE immuno_disease2ligand_refs
    ADD CONSTRAINT immuno_disease2ligand_refs_pk PRIMARY KEY (immuno_disease2ligand_id, reference_id);


ALTER TABLE immuno_disease2object
    ADD CONSTRAINT immuno_disease2object_pk PRIMARY KEY (immuno_disease2object_id);


ALTER TABLE immuno_disease2object_refs
    ADD CONSTRAINT immuno_disease2object_refs_pk PRIMARY KEY (immuno_disease2object_id, reference_id);


ALTER TABLE inn
    ADD CONSTRAINT inn_pk PRIMARY KEY (inn_number);


ALTER TABLE interaction_affinity_refs
    ADD CONSTRAINT interaction_affinity_refs_pk PRIMARY KEY (interaction_id, reference_id);



ALTER TABLE interaction
    ADD CONSTRAINT interaction_pkey PRIMARY KEY (interaction_id);


ALTER TABLE introduction
    ADD CONSTRAINT introduction_pk PRIMARY KEY (family_id);


ALTER TABLE iuphar2discoverx
    ADD CONSTRAINT iuphar2discoverx_pk PRIMARY KEY (object_id, cat_no);


ALTER TABLE lgic
    ADD CONSTRAINT lgic_pk PRIMARY KEY (object_id);


ALTER TABLE ligand2inn
    ADD CONSTRAINT ligand2inn_pk PRIMARY KEY (ligand_id, inn_number);


ALTER TABLE ligand2meshpharmacology
    ADD CONSTRAINT ligand2meshpharmacology_pk PRIMARY KEY (ligand_id, mesh_term);


ALTER TABLE ligand2subunit
    ADD CONSTRAINT ligand2subunit_pk PRIMARY KEY (ligand_id, subunit_id);


ALTER TABLE ligand2synonym
    ADD CONSTRAINT ligand2synonym_pk PRIMARY KEY (ligand2synonym_id);


ALTER TABLE ligand2synonym_refs
    ADD CONSTRAINT ligand2synonym_refs_pk PRIMARY KEY (ligand2synonym_id, reference_id);

ALTER TABLE ligand_cluster
    ADD CONSTRAINT ligand_cluster_pkey1 PRIMARY KEY (ligand_id);


ALTER TABLE ligand_database_link
    ADD CONSTRAINT ligand_database_link_pk PRIMARY KEY (ligand_database_link_id);


ALTER TABLE ligand_physchem
    ADD CONSTRAINT ligand_physchem_pk PRIMARY KEY (ligand_id);


ALTER TABLE ligand
    ADD CONSTRAINT ligand_pk PRIMARY KEY (ligand_id);


ALTER TABLE ligand_structure
    ADD CONSTRAINT ligand_structure_pk PRIMARY KEY (ligand_id);


ALTER TABLE list_ligand
    ADD CONSTRAINT list_ligand_pk PRIMARY KEY (object_id, ligand_id);


ALTER TABLE multimer
    ADD CONSTRAINT multimer_pk PRIMARY KEY (object_id);


ALTER TABLE mutation
    ADD CONSTRAINT mutation_pkey PRIMARY KEY (mutation_id);


ALTER TABLE mutation_refs
    ADD CONSTRAINT mutation_refs_pk PRIMARY KEY (mutation_id, reference_id);


ALTER TABLE nhr
    ADD CONSTRAINT nhr_pk PRIMARY KEY (object_id);


ALTER TABLE object2go_process
    ADD CONSTRAINT object2go_process_pk PRIMARY KEY (object_id, go_process_id);


ALTER TABLE object2reaction
    ADD CONSTRAINT object2reaction_pk PRIMARY KEY (object_id, reaction_id);


ALTER TABLE object
    ADD CONSTRAINT object_pk PRIMARY KEY (object_id);


ALTER TABLE object_vectors
    ADD CONSTRAINT object_vectors_pkey PRIMARY KEY (object_id);


ALTER TABLE ontology
    ADD CONSTRAINT ontology_pk PRIMARY KEY (ontology_id);


ALTER TABLE ontology_term
    ADD CONSTRAINT ontology_term_pk PRIMARY KEY (ontology_id, term_id);


ALTER TABLE other_ic
    ADD CONSTRAINT other_ic_pk PRIMARY KEY (object_id);


ALTER TABLE other_protein
    ADD CONSTRAINT other_protein_pk PRIMARY KEY (object_id);


ALTER TABLE pathophysiology
    ADD CONSTRAINT pathophysiology_pkey PRIMARY KEY (pathophysiology_id);


ALTER TABLE pathophysiology_refs
    ADD CONSTRAINT pathophysiology_refs_pk PRIMARY KEY (pathophysiology_id, reference_id);


ALTER TABLE pdb_structure
    ADD CONSTRAINT pdb_structure_pk PRIMARY KEY (pdb_structure_id);


ALTER TABLE pdb_structure_refs
    ADD CONSTRAINT pdb_structure_refs_pk PRIMARY KEY (pdb_structure_id, reference_id);


ALTER TABLE peptide_ligand_cluster
    ADD CONSTRAINT peptide_ligand_cluster_pkey PRIMARY KEY (ligand_id);


ALTER TABLE peptide_ligand_sequence_cluster
    ADD CONSTRAINT peptide_ligand_sequence_cluster_pk PRIMARY KEY (ligand_id);


ALTER TABLE peptide
    ADD CONSTRAINT peptide_pk PRIMARY KEY (ligand_id);


ALTER TABLE physiological_function
    ADD CONSTRAINT physiological_function_pkey PRIMARY KEY (physiological_function_id);


ALTER TABLE physiological_function_refs
    ADD CONSTRAINT physiological_function_refs_pk PRIMARY KEY (physiological_function_id, reference_id);


ALTER TABLE precursor2peptide
    ADD CONSTRAINT precursor2peptide_pk PRIMARY KEY (precursor_id, ligand_id);


ALTER TABLE precursor2synonym
    ADD CONSTRAINT precursor2synonym_pk PRIMARY KEY (precursor2synonym_id);


ALTER TABLE precursor
    ADD CONSTRAINT precursor_pk PRIMARY KEY (precursor_id);


ALTER TABLE transduction
    ADD CONSTRAINT primary_1 PRIMARY KEY (transduction_id);


ALTER TABLE primary_regulator
    ADD CONSTRAINT primary_regulator_pk PRIMARY KEY (primary_regulator_id);


ALTER TABLE primary_regulator_refs
    ADD CONSTRAINT primary_regulator_refs_pk PRIMARY KEY (primary_regulator_id, reference_id);


ALTER TABLE process_assoc
    ADD CONSTRAINT process_assoc_pk PRIMARY KEY (process_assoc_id);


ALTER TABLE process_assoc_refs
    ADD CONSTRAINT process_assoc_refs_pk PRIMARY KEY (process_assoc_id, reference_id);


ALTER TABLE prodrug
    ADD CONSTRAINT prodrug_pk PRIMARY KEY (prodrug_ligand_id, drug_ligand_id);


ALTER TABLE product
    ADD CONSTRAINT product_pk PRIMARY KEY (product_id);


ALTER TABLE product_refs
    ADD CONSTRAINT product_refs_pk PRIMARY KEY (product_id, reference_id);


ALTER TABLE query_table
    ADD CONSTRAINT query_table_pkey PRIMARY KEY (query_id);


ALTER TABLE reaction
    ADD CONSTRAINT reaction_pk PRIMARY KEY (reaction_id);


ALTER TABLE receptor2family
    ADD CONSTRAINT receptor2family_pk PRIMARY KEY (object_id, family_id);


ALTER TABLE receptor2subunit
    ADD CONSTRAINT receptor2subunit_pk PRIMARY KEY (receptor_id, subunit_id);


ALTER TABLE receptor_basic
    ADD CONSTRAINT receptor_basic_pk PRIMARY KEY (object_id);


ALTER TABLE reference2ligand
    ADD CONSTRAINT reference2ligand_pk PRIMARY KEY (reference_id, ligand_id);


ALTER TABLE reference
    ADD CONSTRAINT reference_pkey PRIMARY KEY (reference_id);


ALTER TABLE screen_interaction
    ADD CONSTRAINT screen_interaction_pk PRIMARY KEY (screen_interaction_id);


ALTER TABLE screen
    ADD CONSTRAINT screen_pk PRIMARY KEY (screen_id);


ALTER TABLE screen_refs
    ADD CONSTRAINT screen_refs_pk PRIMARY KEY (screen_id, reference_id);


ALTER TABLE selectivity
    ADD CONSTRAINT selectivity_pkey PRIMARY KEY (selectivity_id);


ALTER TABLE selectivity_refs
    ADD CONSTRAINT selectivity_refs_pk PRIMARY KEY (selectivity_id, reference_id);


ALTER TABLE species
    ADD CONSTRAINT species_pk PRIMARY KEY (species_id);


ALTER TABLE specific_reaction
    ADD CONSTRAINT specific_reaction_pk PRIMARY KEY (specific_reaction_id);


ALTER TABLE specific_reaction_refs
    ADD CONSTRAINT specific_reaction_refs_pk PRIMARY KEY (specific_reaction_id, reference_id);


ALTER TABLE structural_info
    ADD CONSTRAINT structural_info_pk PRIMARY KEY (structural_info_id);


ALTER TABLE structural_info_refs
    ADD CONSTRAINT structural_info_refs_pk PRIMARY KEY (structural_info_id, reference_id);


ALTER TABLE subcommittee
    ADD CONSTRAINT subcommittee_pk PRIMARY KEY (contributor_id, family_id);


ALTER TABLE subgoal_arguments_org
    ADD CONSTRAINT subgoal_arguments_pkey PRIMARY KEY (subgoal_names);


ALTER TABLE subgoal_arguments
    ADD CONSTRAINT subgoal_arguments_pkey1 PRIMARY KEY (subgoal_names);


ALTER TABLE substrate
    ADD CONSTRAINT substrate_pk PRIMARY KEY (substrate_id);


ALTER TABLE substrate_refs
    ADD CONSTRAINT substrate_refs_pk PRIMARY KEY (substrate_id, reference_id);


ALTER TABLE synonym
    ADD CONSTRAINT synonym_pk PRIMARY KEY (synonym_id);


ALTER TABLE synonym_refs
    ADD CONSTRAINT synonym_refs_pk PRIMARY KEY (synonym_id, reference_id);


ALTER TABLE target_gene
    ADD CONSTRAINT target_gene_pk PRIMARY KEY (target_gene_id);


ALTER TABLE target_gene_refs
    ADD CONSTRAINT target_gene_refs_pk PRIMARY KEY (target_gene_id, reference_id);


ALTER TABLE target_ligand_same_entity
    ADD CONSTRAINT target_ligand_same_entity_pk PRIMARY KEY (object_id, ligand_id);


ALTER TABLE tissue_distribution
    ADD CONSTRAINT tissue_distribution_pkey PRIMARY KEY (tissue_distribution_id);


ALTER TABLE tissue_distribution_refs
    ADD CONSTRAINT tissue_distribution_refs_pk PRIMARY KEY (tissue_distribution_id, reference_id);


ALTER TABLE tissue
    ADD CONSTRAINT tissue_pk PRIMARY KEY (tissue_id);


ALTER TABLE transduction_refs
    ADD CONSTRAINT transduction_refs_pk PRIMARY KEY (transduction_id, reference_id);


ALTER TABLE transporter
    ADD CONSTRAINT transporter_pk PRIMARY KEY (object_id);


ALTER TABLE variant2database_link
    ADD CONSTRAINT variant2database_link_pk PRIMARY KEY (variant_id, database_link_id);


ALTER TABLE variant
    ADD CONSTRAINT variant_pkey PRIMARY KEY (variant_id);


ALTER TABLE variant_refs
    ADD CONSTRAINT variant_refs_pk PRIMARY KEY (variant_id, reference_id);


ALTER TABLE version
    ADD CONSTRAINT version_pk PRIMARY KEY (version_number);


ALTER TABLE vgic
    ADD CONSTRAINT vgic_pk PRIMARY KEY (object_id);


ALTER TABLE voltage_dep_activation_refs
    ADD CONSTRAINT voltage_dep_activation_refs_pk PRIMARY KEY (voltage_dependence_id, reference_id);


ALTER TABLE voltage_dep_deactivation_refs
    ADD CONSTRAINT voltage_dep_deactivation_refs_pk PRIMARY KEY (voltage_dependence_id, reference_id);


ALTER TABLE voltage_dep_inactivation_refs
    ADD CONSTRAINT voltage_dep_inactivation_refs_pk PRIMARY KEY (voltage_dependence_id, reference_id);


ALTER TABLE voltage_dependence
    ADD CONSTRAINT voltage_dependence_pkey PRIMARY KEY (voltage_dependence_id);


ALTER TABLE xenobiotic_expression
    ADD CONSTRAINT xenobiotic_expression_pk PRIMARY KEY (xenobiotic_expression_id);


ALTER TABLE xenobiotic_expression_refs
    ADD CONSTRAINT xenobiotic_expression_refs_pk PRIMARY KEY (xenobiotic_expression_id, reference_id);


ALTER TABLE altered_expression_refs
    ADD CONSTRAINT altered_expression_altered_expression_refs_fk FOREIGN KEY (altered_expression_id) REFERENCES altered_expression(altered_expression_id);


ALTER TABLE altered_expression
    ADD CONSTRAINT alterered_expression_object_id_fkey FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE associated_protein_refs
    ADD CONSTRAINT associated_protein_associated_protein_refs_fk FOREIGN KEY (associated_protein_id) REFERENCES associated_protein(associated_protein_id);


ALTER TABLE binding_partner_refs
    ADD CONSTRAINT binding_partner_binding_partner_refs_fk FOREIGN KEY (binding_partner_id) REFERENCES binding_partner(binding_partner_id);


ALTER TABLE immuno2co_celltype
    ADD CONSTRAINT cellonto_id_fk FOREIGN KEY (cellonto_id) REFERENCES co_celltype(cellonto_id);


ALTER TABLE celltype_assoc_refs
    ADD CONSTRAINT celltype_assoc_celltype_assoc_refs_fk FOREIGN KEY (celltype_assoc_id) REFERENCES celltype_assoc(celltype_assoc_id);


ALTER TABLE celltype_assoc_colist
    ADD CONSTRAINT celltype_assoc_id_fk FOREIGN KEY (celltype_assoc_id) REFERENCES celltype_assoc(celltype_assoc_id);


ALTER TABLE cellular_location_refs
    ADD CONSTRAINT cellular_location_cellular_location_refs_fk FOREIGN KEY (cellular_location_id) REFERENCES cellular_location(cellular_location_id);


ALTER TABLE co_celltype_isa
    ADD CONSTRAINT child_id_fk FOREIGN KEY (child_id) REFERENCES co_celltype(co_celltype_id);


ALTER TABLE celltype_assoc_colist
    ADD CONSTRAINT co_celltype_id_fk FOREIGN KEY (co_celltype_id) REFERENCES co_celltype(co_celltype_id);


ALTER TABLE cofactor_refs
    ADD CONSTRAINT cofactor_cofactor_refs_fk FOREIGN KEY (cofactor_id) REFERENCES cofactor(cofactor_id);


ALTER TABLE contributor2committee
    ADD CONSTRAINT committee_contributor2committee_fk FOREIGN KEY (committee_id) REFERENCES committee(committee_id);


ALTER TABLE conductance_refs
    ADD CONSTRAINT conductance_conductance_refs_fk FOREIGN KEY (conductance_id) REFERENCES conductance(conductance_id);


ALTER TABLE conductance_states_refs
    ADD CONSTRAINT conductance_states_conductance_states_refs_fk FOREIGN KEY (conductance_states_id) REFERENCES conductance_states(conductance_states_id);


ALTER TABLE contributor2ligand
    ADD CONSTRAINT contributor2ligand_ligand_id_fkey FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE contributor2committee
    ADD CONSTRAINT contributor_contributor2committee_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE contributor2family
    ADD CONSTRAINT contributor_contributor2family_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE contributor2intro
    ADD CONSTRAINT contributor_contributor2intro_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE contributor2object
    ADD CONSTRAINT contributor_contributor2object_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE contributor_link
    ADD CONSTRAINT contributor_contributor_link_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE subcommittee
    ADD CONSTRAINT contributor_subcommittee_fk FOREIGN KEY (contributor_id) REFERENCES contributor(contributor_id);


ALTER TABLE coregulator_refs
    ADD CONSTRAINT coregulator_coregulator_refs_fk FOREIGN KEY (coregulator_id) REFERENCES coregulator(coregulator_id);


ALTER TABLE coregulator
    ADD CONSTRAINT coregulator_gene_coregulator_fk FOREIGN KEY (coregulator_gene_id) REFERENCES coregulator_gene(coregulator_gene_id);


ALTER TABLE database_link
    ADD CONSTRAINT database_database_link_fk FOREIGN KEY (database_id) REFERENCES database(database_id);

ALTER TABLE database_link
    ADD CONSTRAINT database_object_link_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE disease_database_link
    ADD CONSTRAINT database_disease_database_link_fk FOREIGN KEY (database_id) REFERENCES database(database_id);


ALTER TABLE ligand_database_link
    ADD CONSTRAINT database_ligand_database_link_fk FOREIGN KEY (database_id) REFERENCES database(database_id);


ALTER TABLE variant2database_link
    ADD CONSTRAINT database_link_variant2database_link_fk FOREIGN KEY (database_link_id) REFERENCES database_link(database_link_id);


ALTER TABLE iuphar2discoverx
    ADD CONSTRAINT discoverx_iuphar2discoverx_fk FOREIGN KEY (cat_no) REFERENCES discoverx(cat_no);


ALTER TABLE disease_synonym2database_link
    ADD CONSTRAINT disease2synonym_disease_synonym2database_link_fk FOREIGN KEY (disease2synonym_id) REFERENCES disease2synonym(disease2synonym_id);


ALTER TABLE disease_synonym2database_link
    ADD CONSTRAINT disease_database_link_disease_synonym2database_link_fk FOREIGN KEY (disease_database_link_id) REFERENCES disease_database_link(disease_database_link_id);


ALTER TABLE disease2synonym
    ADD CONSTRAINT disease_disease2synonym_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE disease_database_link
    ADD CONSTRAINT disease_disease_database_link_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE drug2disease
    ADD CONSTRAINT disease_drug2disease_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE immuno_disease2ligand
    ADD CONSTRAINT disease_immuno_disease2ligand_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE immuno_disease2object
    ADD CONSTRAINT disease_immuno_disease2object_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE pathophysiology
    ADD CONSTRAINT disease_pathophysiology_fk FOREIGN KEY (disease_id) REFERENCES disease(disease_id);


ALTER TABLE dna_binding_refs
    ADD CONSTRAINT dna_binding_dna_binding_refs_fk FOREIGN KEY (dna_binding_id) REFERENCES dna_binding(dna_binding_id);


ALTER TABLE cofactor
    ADD CONSTRAINT enzyme_cofactor_fk FOREIGN KEY (object_id) REFERENCES enzyme(object_id);


ALTER TABLE expression_level
    ADD CONSTRAINT expression_experiment_expression_level_fk FOREIGN KEY (expression_experiment_id) REFERENCES expression_experiment(expression_experiment_id);


ALTER TABLE expression_pathophysiology_refs
    ADD CONSTRAINT expression_pathophysiology_expression_pathophysiology_refs_fk FOREIGN KEY (expression_pathophysiology_id) REFERENCES expression_pathophysiology(expression_pathophysiology_id);


ALTER TABLE committee
    ADD CONSTRAINT family_committee_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE contributor2family
    ADD CONSTRAINT family_contributor2family_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE deleted_family
    ADD CONSTRAINT family_deleted_family_fk FOREIGN KEY (new_family_id) REFERENCES family(family_id);


ALTER TABLE grac_family_text
    ADD CONSTRAINT family_grac_family_text_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE grac_further_reading
    ADD CONSTRAINT family_grac_further_reading_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE "grouping"
    ADD CONSTRAINT family_grouping_fk FOREIGN KEY (group_id) REFERENCES family(family_id);


ALTER TABLE "grouping"
    ADD CONSTRAINT family_grouping_fk1 FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE introduction
    ADD CONSTRAINT family_introduction_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE receptor2family
    ADD CONSTRAINT family_receptor2family_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE subcommittee
    ADD CONSTRAINT family_subcommittee_fk FOREIGN KEY (family_id) REFERENCES family(family_id);


ALTER TABLE functional_assay_refs
    ADD CONSTRAINT functional_assay_functional_assay_refs_fk FOREIGN KEY (functional_assay_id) REFERENCES functional_assay(functional_assay_id);

ALTER TABLE grac_ligand_rank_potency_refs
    ADD CONSTRAINT grac_ligand_rank_potency_grac_ligand_rank_potency_refs_fk FOREIGN KEY (grac_ligand_rank_potency_id) REFERENCES grac_ligand_rank_potency(grac_ligand_rank_potency_id);


ALTER TABLE gtip2go_process
    ADD CONSTRAINT gtip_process_id_fk FOREIGN KEY (gtip_process_id) REFERENCES gtip_process(gtip_process_id);


ALTER TABLE process_assoc
    ADD CONSTRAINT gtip_process_id_fk FOREIGN KEY (gtip_process_id) REFERENCES gtip_process(gtip_process_id);


ALTER TABLE celltype_assoc
    ADD CONSTRAINT immuno_celltype_celltype_assoc_fk FOREIGN KEY (immuno_celltype_id) REFERENCES immuno_celltype(immuno_celltype_id);


ALTER TABLE immuno2co_celltype
    ADD CONSTRAINT immuno_celltype_id_fk FOREIGN KEY (immuno_celltype_id) REFERENCES immuno_celltype(immuno_celltype_id);


ALTER TABLE immuno_disease2ligand_refs
    ADD CONSTRAINT immuno_disease2ligand_immuno_disease2ligand_refs_fk FOREIGN KEY (immuno_disease2ligand_id) REFERENCES immuno_disease2ligand(immuno_disease2ligand_id);


ALTER TABLE immuno_disease2object_refs
    ADD CONSTRAINT immuno_disease2object_immuno_disease2object_refs_fk FOREIGN KEY (immuno_disease2object_id) REFERENCES immuno_disease2object(immuno_disease2object_id);


ALTER TABLE ligand2inn
    ADD CONSTRAINT inn_ligand2inn_fk FOREIGN KEY (inn_number) REFERENCES inn(inn_number);


ALTER TABLE interaction_affinity_refs
    ADD CONSTRAINT interaction_interaction_affinity_refs_fk FOREIGN KEY (interaction_id) REFERENCES interaction(interaction_id);


ALTER TABLE contributor2intro
    ADD CONSTRAINT introduction_contributor2intro_fk FOREIGN KEY (family_id) REFERENCES introduction(family_id);


ALTER TABLE ligand2synonym_refs
    ADD CONSTRAINT ligand2synonym_ligand2synonym_refs_fk FOREIGN KEY (ligand2synonym_id) REFERENCES ligand2synonym(ligand2synonym_id);


ALTER TABLE analogue_cluster
    ADD CONSTRAINT ligand_analogue_cluster_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE cofactor
    ADD CONSTRAINT ligand_cofactor_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE drug2disease
    ADD CONSTRAINT ligand_drug2disease_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE interaction
    ADD CONSTRAINT ligand_interaction_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE interaction
    ADD CONSTRAINT ligand_interaction_fk1 FOREIGN KEY (target_ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand2inn
    ADD CONSTRAINT ligand_ligand2inn_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand2meshpharmacology
    ADD CONSTRAINT ligand_ligand2meshpharmacology_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand2subunit
    ADD CONSTRAINT ligand_ligand2subunit_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand2subunit
    ADD CONSTRAINT ligand_ligand2subunit_fk1 FOREIGN KEY (subunit_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand2synonym
    ADD CONSTRAINT ligand_ligand2synonym_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand_cluster
    ADD CONSTRAINT ligand_ligand_cluster_fk2 FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE ligand_database_link
    ADD CONSTRAINT ligand_ligand_database_link_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE list_ligand
    ADD CONSTRAINT ligand_ligand_list_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE pdb_structure
    ADD CONSTRAINT ligand_pdb_structure_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE peptide
    ADD CONSTRAINT ligand_peptide_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE peptide_ligand_sequence_cluster
    ADD CONSTRAINT ligand_peptide_ligand_cluster_1_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE peptide_ligand_cluster
    ADD CONSTRAINT ligand_peptide_ligand_cluster_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE prodrug
    ADD CONSTRAINT ligand_prodrug_fk FOREIGN KEY (prodrug_ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE prodrug
    ADD CONSTRAINT ligand_prodrug_fk1 FOREIGN KEY (drug_ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE product
    ADD CONSTRAINT ligand_product_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE reference2ligand
    ADD CONSTRAINT ligand_reference2ligand_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE screen_interaction
    ADD CONSTRAINT ligand_screen_interaction_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE substrate
    ADD CONSTRAINT ligand_substrate_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE target_ligand_same_entity
    ADD CONSTRAINT ligand_target_ligand_same_entity_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE mutation_refs
    ADD CONSTRAINT mutation_mutation_refs_fk FOREIGN KEY (mutation_id) REFERENCES mutation(mutation_id);


ALTER TABLE accessory_protein
    ADD CONSTRAINT object_accessory_protein_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE associated_protein
    ADD CONSTRAINT object_associated_protein_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE associated_protein
    ADD CONSTRAINT object_associated_protein_fk1 FOREIGN KEY (associated_object_id) REFERENCES object(object_id);


ALTER TABLE binding_partner
    ADD CONSTRAINT object_binding_partner_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE binding_partner
    ADD CONSTRAINT object_binding_partner_partner_id_fk FOREIGN KEY (partner_object_id) REFERENCES object(object_id);

ALTER TABLE catalytic_receptor
    ADD CONSTRAINT object_catalytic_receptor_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE celltype_assoc
    ADD CONSTRAINT object_celltype_assoc_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE cellular_location
    ADD CONSTRAINT object_cellular_location_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE chembl_cluster
    ADD CONSTRAINT object_chembl_cluster_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE conductance
    ADD CONSTRAINT object_conductance_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE conductance_states
    ADD CONSTRAINT object_conductance_states_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE contributor2object
    ADD CONSTRAINT object_contributor2object_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE coregulator
    ADD CONSTRAINT object_coregulator_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE database_link
    ADD CONSTRAINT object_database_link_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE dna_binding
    ADD CONSTRAINT object_dna_binding_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE enzyme
    ADD CONSTRAINT object_enzyme_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE expression_pathophysiology
    ADD CONSTRAINT object_expression_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE functional_assay
    ADD CONSTRAINT object_functional_assay_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE further_reading
    ADD CONSTRAINT object_further_reading_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE gpcr
    ADD CONSTRAINT object_gpcr_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE grac_functional_characteristics
    ADD CONSTRAINT object_grac_functional_characteristics_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE grac_ligand_rank_potency
    ADD CONSTRAINT object_grac_ligand_rank_potency_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE grac_transduction
    ADD CONSTRAINT object_grac_transduction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE object2go_process
    ADD CONSTRAINT object_id_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE process_assoc
    ADD CONSTRAINT object_id_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE immuno_disease2ligand
    ADD CONSTRAINT object_immuno_disease2ligand_fk FOREIGN KEY (ligand_id) REFERENCES ligand(ligand_id);


ALTER TABLE immuno_disease2object
    ADD CONSTRAINT object_immuno_disease2object_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE interaction
    ADD CONSTRAINT object_interaction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE iuphar2discoverx
    ADD CONSTRAINT object_iuphar2discoverx_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE lgic
    ADD CONSTRAINT object_lgic_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE list_ligand
    ADD CONSTRAINT object_ligand_list_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE multimer
    ADD CONSTRAINT object_multimer_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE mutation
    ADD CONSTRAINT object_mutation_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE nhr
    ADD CONSTRAINT object_nhr_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE object2reaction
    ADD CONSTRAINT object_object2reaction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE other_ic
    ADD CONSTRAINT object_other_ic_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE other_protein
    ADD CONSTRAINT object_other_protein_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE pathophysiology
    ADD CONSTRAINT object_pathophysiology_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE pdb_structure
    ADD CONSTRAINT object_pdb_structure_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE physiological_function
    ADD CONSTRAINT object_physiological_function_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE primary_regulator
    ADD CONSTRAINT object_primary_regulator_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE primary_regulator
    ADD CONSTRAINT object_primary_regulator_regulator_fk FOREIGN KEY (regulator_object_id) REFERENCES object(object_id);


ALTER TABLE product
    ADD CONSTRAINT object_product_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE receptor_basic
    ADD CONSTRAINT object_receptor_basic_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE screen_interaction
    ADD CONSTRAINT object_screen_interaction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE selectivity
    ADD CONSTRAINT object_selectivity_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE specific_reaction
    ADD CONSTRAINT object_specific_reaction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE structural_info
    ADD CONSTRAINT object_structural_info_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE substrate
    ADD CONSTRAINT object_substrate_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE receptor2subunit
    ADD CONSTRAINT object_subunits_fk FOREIGN KEY (receptor_id) REFERENCES object(object_id);


ALTER TABLE receptor2subunit
    ADD CONSTRAINT object_subunits_fk_1 FOREIGN KEY (subunit_id) REFERENCES object(object_id);


ALTER TABLE synonym
    ADD CONSTRAINT object_synonym_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE target_gene
    ADD CONSTRAINT object_target_gene_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE target_ligand_same_entity
    ADD CONSTRAINT object_target_ligand_same_entity_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE tissue_distribution
    ADD CONSTRAINT object_tissue_distribution_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE transduction
    ADD CONSTRAINT object_transduction_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE transporter
    ADD CONSTRAINT object_transporter_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE variant
    ADD CONSTRAINT object_variant_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE vgic
    ADD CONSTRAINT object_vgic_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE voltage_dependence
    ADD CONSTRAINT object_voltage_dependence_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE xenobiotic_expression
    ADD CONSTRAINT object_xenobiotic_expression_fk FOREIGN KEY (object_id) REFERENCES object(object_id);


ALTER TABLE ontology_term
    ADD CONSTRAINT ontology_ontology_term_fk FOREIGN KEY (ontology_id) REFERENCES ontology(ontology_id);



ALTER TABLE co_celltype_isa
    ADD CONSTRAINT parent_id_fk FOREIGN KEY (parent_id) REFERENCES co_celltype(co_celltype_id);


ALTER TABLE mutation
    ADD CONSTRAINT pathophysiology_mutation_fk FOREIGN KEY (pathophysiology_id) REFERENCES pathophysiology(pathophysiology_id);


ALTER TABLE pathophysiology_refs
    ADD CONSTRAINT pathophysiology_pathophysiology_refs_fk FOREIGN KEY (pathophysiology_id) REFERENCES pathophysiology(pathophysiology_id);


ALTER TABLE pdb_structure_refs
    ADD CONSTRAINT pdb_structure_pdb_structure_refs_fk FOREIGN KEY (pdb_structure_id) REFERENCES pdb_structure(pdb_structure_id);


ALTER TABLE precursor2peptide
    ADD CONSTRAINT peptide_precursor2peptide_fk FOREIGN KEY (ligand_id) REFERENCES peptide(ligand_id);


ALTER TABLE physiological_function_refs
    ADD CONSTRAINT physiological_function_physiological_function_refs_fk FOREIGN KEY (physiological_function_id) REFERENCES physiological_function(physiological_function_id);


ALTER TABLE precursor2peptide
    ADD CONSTRAINT precursor_precursor2peptide_fk FOREIGN KEY (precursor_id) REFERENCES precursor(precursor_id);


ALTER TABLE precursor2synonym
    ADD CONSTRAINT precursor_precursor2synonym_fk FOREIGN KEY (precursor_id) REFERENCES precursor(precursor_id);


ALTER TABLE primary_regulator_refs
    ADD CONSTRAINT primary_regulator_primary_regulator_refs_fk FOREIGN KEY (primary_regulator_id) REFERENCES primary_regulator(primary_regulator_id);

ALTER TABLE process_assoc_refs
    ADD CONSTRAINT process_assoc_process_assoc_refs_fk FOREIGN KEY (process_assoc_id) REFERENCES process_assoc(process_assoc_id);


ALTER TABLE product_refs
    ADD CONSTRAINT product_product_refs_fk FOREIGN KEY (product_id) REFERENCES product(product_id);


ALTER TABLE object2reaction
    ADD CONSTRAINT reaction_object2reaction_fk FOREIGN KEY (reaction_id) REFERENCES reaction(reaction_id);


ALTER TABLE specific_reaction
    ADD CONSTRAINT reaction_specific_reaction_fk FOREIGN KEY (reaction_id) REFERENCES reaction(reaction_id);


ALTER TABLE receptor2family
    ADD CONSTRAINT receptor_basic_receptor2family_fk FOREIGN KEY (object_id) REFERENCES receptor_basic(object_id);


ALTER TABLE altered_expression_refs
    ADD CONSTRAINT reference_altered_expression_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE associated_protein_refs
    ADD CONSTRAINT reference_associated_protein_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE binding_partner_refs
    ADD CONSTRAINT reference_binding_partner_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE celltype_assoc_refs
    ADD CONSTRAINT reference_celltype_assoc_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE cellular_location_refs
    ADD CONSTRAINT reference_cellular_location_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE cofactor_refs
    ADD CONSTRAINT reference_cofactor_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE conductance_refs
    ADD CONSTRAINT reference_conductance_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE conductance_states_refs
    ADD CONSTRAINT reference_conductance_states_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE coregulator_refs
    ADD CONSTRAINT reference_coregulator_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE dna_binding_refs
    ADD CONSTRAINT reference_dna_binding_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE expression_pathophysiology_refs
    ADD CONSTRAINT reference_expression_pathophysiology_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE functional_assay_refs
    ADD CONSTRAINT reference_functional_assay_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE further_reading
    ADD CONSTRAINT reference_further_reading_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE grac_further_reading
    ADD CONSTRAINT reference_grac_further_reading_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE grac_ligand_rank_potency_refs
    ADD CONSTRAINT reference_grac_ligand_rank_potency_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE immuno_disease2ligand_refs
    ADD CONSTRAINT reference_immuno_disease2ligand_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE immuno_disease2object_refs
    ADD CONSTRAINT reference_immuno_disease2object_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE interaction_affinity_refs
    ADD CONSTRAINT reference_interaction_affinity_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE ligand2synonym_refs
    ADD CONSTRAINT reference_ligand2synonym_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE mutation_refs
    ADD CONSTRAINT reference_mutation_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE pathophysiology_refs
    ADD CONSTRAINT reference_pathophysiology_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE pdb_structure_refs
    ADD CONSTRAINT reference_pdb_structure_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE physiological_function_refs
    ADD CONSTRAINT reference_physiological_function_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE primary_regulator_refs
    ADD CONSTRAINT reference_primary_regulator_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE process_assoc_refs
    ADD CONSTRAINT reference_process_assoc_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE product_refs
    ADD CONSTRAINT reference_product_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE reference2ligand
    ADD CONSTRAINT reference_reference2ligand_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE screen_refs
    ADD CONSTRAINT reference_screen_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE selectivity_refs
    ADD CONSTRAINT reference_selectivity_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE specific_reaction_refs
    ADD CONSTRAINT reference_specific_reaction_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE structural_info_refs
    ADD CONSTRAINT reference_structural_info_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE substrate_refs
    ADD CONSTRAINT reference_substrate_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE synonym_refs
    ADD CONSTRAINT reference_synonym_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE target_gene_refs
    ADD CONSTRAINT reference_target_gene_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE tissue_distribution_refs
    ADD CONSTRAINT reference_tissue_distribution_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE transduction_refs
    ADD CONSTRAINT reference_transduction_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE variant_refs
    ADD CONSTRAINT reference_variant_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE voltage_dep_activation_refs
    ADD CONSTRAINT reference_voltage_dep_activation_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE voltage_dep_deactivation_refs
    ADD CONSTRAINT reference_voltage_dep_deactivation_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE voltage_dep_inactivation_refs
    ADD CONSTRAINT reference_voltage_dep_inactivation_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE xenobiotic_expression_refs
    ADD CONSTRAINT reference_xenobiotic_expression_refs_fk FOREIGN KEY (reference_id) REFERENCES reference(reference_id);


ALTER TABLE screen_interaction
    ADD CONSTRAINT screen_screen_interaction_fk FOREIGN KEY (screen_id) REFERENCES screen(screen_id);


ALTER TABLE screen_refs
    ADD CONSTRAINT screen_screen_refs_fk FOREIGN KEY (screen_id) REFERENCES screen(screen_id);


ALTER TABLE selectivity_refs
    ADD CONSTRAINT selectivity_selectivity_refs_fk FOREIGN KEY (selectivity_id) REFERENCES selectivity(selectivity_id);


ALTER TABLE allele
    ADD CONSTRAINT species_allele_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE altered_expression
    ADD CONSTRAINT species_altered_expression_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE cofactor
    ADD CONSTRAINT species_cofactor_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE conductance
    ADD CONSTRAINT species_conductance_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE coregulator_gene
    ADD CONSTRAINT species_coregulator_gene_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE database_link
    ADD CONSTRAINT species_database_link_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE discoverx
    ADD CONSTRAINT species_discoverx_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE expression_experiment
    ADD CONSTRAINT species_expression_experiment_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE expression_pathophysiology
    ADD CONSTRAINT species_expression_pathophysiology_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE functional_assay
    ADD CONSTRAINT species_functional_assay_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE grac_ligand_rank_potency
    ADD CONSTRAINT species_grac_ligand_rank_potency_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE interaction
    ADD CONSTRAINT species_interaction_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE ligand_database_link
    ADD CONSTRAINT species_ligand_database_link_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE mutation
    ADD CONSTRAINT species_mutation_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE pdb_structure
    ADD CONSTRAINT species_pdb_structure_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE physiological_function
    ADD CONSTRAINT species_physiological_function_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE precursor
    ADD CONSTRAINT species_precursor_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE product
    ADD CONSTRAINT species_product_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE screen_interaction
    ADD CONSTRAINT species_screen_interaction_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE selectivity
    ADD CONSTRAINT species_selectivity_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE structural_info
    ADD CONSTRAINT species_structural_info_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE substrate
    ADD CONSTRAINT species_substrate_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE target_gene
    ADD CONSTRAINT species_target_gene_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE tissue_distribution
    ADD CONSTRAINT species_tissue_distribution_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE variant
    ADD CONSTRAINT species_variant_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE voltage_dependence
    ADD CONSTRAINT species_voltage_dependence_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE xenobiotic_expression
    ADD CONSTRAINT species_xenobiotic_expression_fk FOREIGN KEY (species_id) REFERENCES species(species_id);


ALTER TABLE specific_reaction_refs
    ADD CONSTRAINT specific_reaction_specific_reaction_refs_fk FOREIGN KEY (specific_reaction_id) REFERENCES specific_reaction(specific_reaction_id);


ALTER TABLE expression_level
    ADD CONSTRAINT structural_info_expression_level_fk FOREIGN KEY (structural_info_id) REFERENCES structural_info(structural_info_id);


ALTER TABLE structural_info_refs
    ADD CONSTRAINT structural_info_structural_info_refs_fk FOREIGN KEY (structural_info_id) REFERENCES structural_info(structural_info_id);


ALTER TABLE substrate_refs
    ADD CONSTRAINT substrate_substrate_refs_fk FOREIGN KEY (substrate_id) REFERENCES substrate(substrate_id);


ALTER TABLE synonym_refs
    ADD CONSTRAINT synonym_synonym_refs_fk FOREIGN KEY (synonym_id) REFERENCES synonym(synonym_id);


ALTER TABLE target_gene_refs
    ADD CONSTRAINT target_gene_target_gene_refs_fk FOREIGN KEY (target_gene_id) REFERENCES target_gene(target_gene_id);


ALTER TABLE tissue_distribution_refs
    ADD CONSTRAINT tissue_distribution_tissue_distribution_refs_fk FOREIGN KEY (tissue_distribution_id) REFERENCES tissue_distribution(tissue_distribution_id);


ALTER TABLE expression_level
    ADD CONSTRAINT tissue_expression_level_fk FOREIGN KEY (tissue_id) REFERENCES tissue(tissue_id);


ALTER TABLE transduction_refs
    ADD CONSTRAINT transduction_transduction_refs_fk FOREIGN KEY (transduction_id) REFERENCES transduction(transduction_id);


ALTER TABLE variant2database_link
    ADD CONSTRAINT variant_variant2database_link_fk FOREIGN KEY (variant_id) REFERENCES variant(variant_id);


ALTER TABLE variant_refs
    ADD CONSTRAINT variant_variant_refs_fk FOREIGN KEY (variant_id) REFERENCES variant(variant_id);


ALTER TABLE voltage_dep_activation_refs
    ADD CONSTRAINT voltage_dependence_voltage_dep_activation_refs_fk FOREIGN KEY (voltage_dependence_id) REFERENCES voltage_dependence(voltage_dependence_id);

ALTER TABLE xenobiotic_expression_refs
    ADD CONSTRAINT xenobiotic_expression_xenobiotic_expression_refs_fk FOREIGN KEY (xenobiotic_expression_id) REFERENCES xenobiotic_expression(xenobiotic_expression_id);
