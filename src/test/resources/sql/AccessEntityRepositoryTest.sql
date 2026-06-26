insert into "ACTOR" ("ID", "EXT_ID", "CREATION_DT", "MODIFICATION_DT") values (1, 'actor1', null, null);
insert into "FILEOBJECT" ("ID", "EXT_ID") values (1, 'filo01');
insert into "ACCESS" ("ID", "PERMISSIONS", "ROLE", "FILEOBJECT_ID", "ACTOR_ID") values (1, 3, 0, 1, 1);