drop type if exists authorRole;
drop type if exists proposalType;
drop type if exists initiativeState;


create type authorRole as enum ('INITIATOR', 'REPRESENTATIVE', 'RESERVE');
create type proposalType as enum ('LAW', 'PREPARATION');
create type initiativeState as enum ('DRAFT', 'PROPOSAL', 'REVIEW', 'ACCEPTED', 'CANCELED', 'DONE');
create type languageCode as enum ('FI', 'SV');
--create type modificationType as enum ('CREATE', 'UPDATE', 'SEND_TO_OM', 'ACCEPT', 
--    'SEND_TO_VRK', 'CONFIRM', 'DELETED');


create table inituser (
    id bigserial
    , hash varchar(64) constraint inituser_hash_nn not null -- sha256(ssn & sharedSecret)
    , lastLogin timestamp
    , firstNames varchar(256) constraint inituser_firstNames_nn not null
    , lastName varchar(256) constraint inituser_lastName_nn not null
    , dateOfBirth date constraint inituser_dateOfBirth_nn not null
    , vrk boolean default false constraint inituser_vrk_nn not null
    , om boolean default false constraint inituser_om_nn not null

    , constraint inituser_pk primary key (id)
    , constraint inituser_hash_u unique (hash)
);


create table initiative (
    id bigserial
    , modified timestamp constraint initiative_modified_nn not null default now()
    , modifier_id bigint constraint initiative_modifier_id_nn not null
--    , modificationType modificationType constraint initiative_modificationType_nn not null default 'CREATE'
    , state initiativeState constraint initiative_state_nn not null default 'DRAFT'
    , stateComment varchar(4096)
    , stateDate timestamp constraint initiative_stateDate_nn not null default now()

    , acceptanceIdentifier varchar(128)

    , supportCount integer constraint initiative_supportCount_nn not null default 0
    , sentSupportCount integer default 0

    , externalSupportCount integer constraint initiative_externalSupportCount_nn not null default 0

    , verifiedSupportCount integer default 0
    , verified date 
    , verificationIdentifier varchar(128)

    , supportStatementsRemoved timestamp 
    
    , name_fi varchar(512)
    , name_sv varchar(512)
    , startDate date
    , endDate date
    , proposalType proposalType constraint initiative_proposalType_nn not null
    , proposal_fi text
    , proposal_sv text
    , rationale_fi text
    , rationale_sv text
    , primaryLanguage languageCode constraint language_code_nn not null

    , financialSupport boolean constraint initiative_financialSupport_nn not null default false
    , financialSupportURL varchar (4096)
    , supportStatementsOnPaper boolean constraint initiative_supportStatementsOnPaper_nn not null default false
    , supportStatementsInWeb boolean constraint initiative_supportStatementsInWeb_nn not null default false

    , constraint initiative_pk primary key (id)
    , constraint initiative_modifier_id_fk foreign key (modifier_id) references inituser (id)

    -- FIXME: 1) Check that if one value of a given language is set, then all required 
    --        fields of that language are set.
    --        2) Validation based on initiative state
    , constraint initiative_name_chk check (name_fi is not null or name_sv is not null)
    , constraint initiative_proposal_chk check (proposal_fi is not null or proposal_sv is not null)
    , constraint initiative_rationale_chk check (rationale_fi is not null or rationale_sv is not null)
);

    
create table initiative_author (
    initiative_id bigint constraint author_initiative_id_nn not null
    , user_id bigint constraint author_user_id_nn not null
    , created timestamp constraint initiative_modified_nn not null default now()
    , firstNames varchar(256) constraint author_firstNames_nn not null
    , lastName varchar(256) constraint author_lastName_nn not null
    , homeMunicipality_fi varchar(30) constraint homeMunicipality_fi_nn not null
    , homeMunicipality_sv varchar(30) constraint homeMunicipality_sv_nn not null
    , email varchar(256)
    , phone varchar(128)
    , address varchar(1024)
    , initiator boolean constraint author_initiator_nn not null
    , role authorRole constraint author_role_nn not null
    , confirmed timestamp
    , confirmationRequestSent timestamp

    , constraint author_pk primary key (initiative_id, user_id)

    , constraint author_initiative_id_fk foreign key (initiative_id)
        references initiative (id) 

    , constraint author_user_id_fk foreign key (user_id) 
        references inituser (id)

    , constraint author_contact_info_chk check (
        role = 'INITIATOR' 
        or email is not null 
        or phone is not null 
        or address is not null)
);
create index author_user_id_fk on initiative_author (user_id);
-- initiative_id is indexed by primary key (initiative_id, user_id)

create table initiative_invitation (
    id bigserial 
    , initiative_id bigint constraint invitation_id_nn not null 
    , created timestamp constraint invitation_created_nn not null default now()
    , sent timestamp
    , invitationCode varchar(64)
    , role authorRole constraint invitation_role_nn not null
    , email varchar(256) constraint invitation_email_nn not null
    
    , constraint invitation_pk primary key (id)

    , constraint invitation_invitationCode_u unique (initiative_id, invitationCode)
    
    , constraint invitation_initiative_id_fk foreign key (initiative_id) 
        references initiative (id) 
        on delete cascade
);
-- initiative_id is indexed by invitation_invitationCode_u unique (initiative_id, invitationCode)

create table initiative_link (
    id bigserial
    , initiative_id bigint constraint link_initiative_id_nn not null
    , label varchar(128) constraint link_label_nn not null
    , uri varchar(4096) constraint link_uri_nn not null
    
    , constraint link_pk primary key (id)
    
    , constraint link_initiative_id_fk foreign key (initiative_id) 
        references initiative (id)
        on delete cascade
);
create index link_initiative_id_fk on initiative_link (initiative_id);


-- create table initiative_keyword (
--     id bigserial
--     , initiative_id bigint constraint keyword_initiative_id_nn not null
--     , label_fi varchar(128)
--     , label_sv varchar(128)
--     , uri varchar(4096)
--     
--     , constraint keyword_pk primary key (id)
--     
--     , constraint keyword_initiative_id_fk foreign key (initiative_id) 
--         references initiative (id)
--         on delete cascade
-- 
--     , constraint keyword_label_nn check (label_fi is not null or label_sv is not null)
-- );
-- create index keyword_initiative_id_fk on initiative_keyword (initiative_id);


create table support_vote_batch (
    id bigserial
    , initiative_id bigint constraint support_vote_batch_initiative_id_nn not null
    , created timestamp constraint support_vote_created_nn not null default now()
    
    , constraint support_vote_batch_pk primary key (id)

    , constraint support_vote_batch_initiative_id_fk foreign key (initiative_id) 
        references initiative (id)
        on delete cascade
);
create index support_vote_batch_initiative_id_fk on support_vote_batch (initiative_id);

create table support_vote (
    initiative_id bigint constraint support_vote_initiative_id_nn not null
    , supportId varchar(64) constraint support_vote_supportId_nn not null -- base64(sha256(initiative_id & ssn & sharedSecret))
    , details varchar(4096) constraint support_vote_details_nn not null -- aes( name & birthDate & municipality )
    , created timestamp constraint support_vote_created_nn not null default now()
    , batch_id bigint
    
    , constraint support_vote_pk primary key (initiative_id, supportId)
    
    , constraint support_vote_initiative_id_fk foreign key (initiative_id) 
        references initiative (id)

    , constraint support_vote_batch_id_fk foreign key (batch_id) 
        references support_vote_batch (id)
);
create index support_vote_batch_id_fk on support_vote (batch_id);
-- initiative_id is indexed by support_vote_pk primary key (initiative_id, supportId)

-- NOTE: First thing all update scripts should do, is insert it self into schema_version table.
-- If script is alread executed, insert will fail.
create table schema_version (
    script varchar(64) not null
    , executed timestamp not null default now()
    
    , constraint schema_version_script_pk primary key (script)
);
insert into schema_version (script) values ('01_schema.sql');
