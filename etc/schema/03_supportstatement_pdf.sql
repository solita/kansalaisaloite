alter table initiative add supportStatementPdf boolean;
alter table initiative add supportStatementAddress varchar(1024);

insert into schema_version (script) values ('03_supportstatement_pdf.sql');