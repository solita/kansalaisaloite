create table follow_initiative (
  email varchar(100) not null,
  initiative_id bigserial constraint follow_initiative_initiative_id_nn not null,
  unsubscribe_hash varchar(40) constraint unsubscribe_hash_nn not null,

  primary key (initiative_id, email),
  constraint follow_initiative_initiative_id foreign key (initiative_id) references initiative(id),
  constraint follow_initiative_unsubscribe_hash unique(unsubscribe_hash)
);


create index follow_initiative_index on follow_initiative(initiative_id);