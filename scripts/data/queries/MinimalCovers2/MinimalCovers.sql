SELECT family.name, family.type FROM family JOIN introduction ON family.family_id = introduction.family_id JOIN fakeLigand ON fakeLigand.text = introduction.text where family.name="pippo";