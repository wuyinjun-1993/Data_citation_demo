create view v9l2 as select name, family.type FROM family JOIN introduction ON family.family_id = introduction.family_id JOIN fakeLigand ON fakeLigand.text = introduction.text where family.name=@x and family.type=@y;


