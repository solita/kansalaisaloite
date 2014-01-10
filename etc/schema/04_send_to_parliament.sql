alter table initiative add parliamentSentTime date;
alter table initiative add parliamentURL varchar(512);
alter table initiative add parliamentIdentifier varchar(50);

insert into schema_version (script) values ('04_send_to_parliament.sql');