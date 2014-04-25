create table initiative_support_vote_day (
  initiative_id bigint constraint support_vote_day_initiative_id_nn not null,
  support_date date constraint support_vote_day_support_date_nn not null,
  support_count integer constraint support_vote_day_support_count_nn not null,

  constraint support_vote_day_initiative_id_fk foreign key (initiative_id) references initiative (id),
  constraint support_vote_day_pk primary key (initiative_id, support_date)
);

create index support_vote_day_initiative_id_fk on initiative_support_vote_day(initiative_id);