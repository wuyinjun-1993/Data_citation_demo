create view v1 as select family_id from family;
create view v2 as select family_id, name from family;
create view v3 as select family_id, name, last_modified from family;
create view v6 as select type, last_modified from family;
create view v7 as select type from family;
create view v4 as select name from family join introduction on family.family_id = introduction.family_id;
create view v5 as select name, type from family join introduction on family.family_id = introduction.family_id;


