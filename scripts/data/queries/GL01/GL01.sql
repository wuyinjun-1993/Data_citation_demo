SELECT family.family_id, family.name FROM family JOIN introduction ON family.family_id = introduction.family_id WHERE family.last_modified > 200.3;
SELECT family.family_id, family.name FROM family JOIN introduction ON family.family_id = introduction.family_id WHERE family.last_modified > 100.3;
SELECT family.family_id, family.name FROM family JOIN introduction ON family.family_id = introduction.family_id;
SELECT family.family_id FROM family JOIN introduction ON family.family_id = introduction.family_id;
SELECT family.family_id FROM family WHERE family.last_modified <= 200.3;
SELECT family.family_id FROM family WHERE family.last_modified >= 100.3;
SELECT family.family_id FROM family;