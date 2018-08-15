create view v1 as select family_id from family;
create view v3 as select family_id, name, last_modified from family;
create view v2 as select family_id, name from family;
create view v3l as select family_id, name, last_modified from family where family.name=@x and family.last_modified=@y;
create view v6 as select type, last_modified from family;
create view v6l as select type, last_modified from family where family.type=@x and family.last_modified=@y;
create view v7 as select name,  type from family;
create view v7l as select name,  type from family where family.name=@x and family.type=@y;
create view v7l2 as select name,  type from family where family.name=@x;
create view v5l as select  type from family join introduction on family.family_id = introduction.family_id where family.name=@x;
create view v5l2 as select name, type from family join introduction on family.family_id = introduction.family_id where family.type=@x;
create view v5l3 as select name, type, last_modified from family join introduction on family.family_id = introduction.family_id where family.name=@x;
create view v8 as select text from introduction;
create view v4 as select type, name from family join introduction on family.family_id = introduction.family_id;

