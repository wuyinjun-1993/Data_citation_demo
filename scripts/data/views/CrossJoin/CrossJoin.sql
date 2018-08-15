create view v1 as select family.name from family;
create view v3 as select family.name FROM family JOIN introduction ON family.family_id = introduction.family_id;

