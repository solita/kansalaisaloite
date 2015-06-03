drop type if exists reviewHistoryType;
create type reviewHistoryType as enum ('REVIEW_REJECT', 'REVIEW_ACCEPT', 'REVIEW_SENT', 'REVIEW_COMMENT');

create table review_history (
  id bigserial,
  initiative_id bigserial constraint review_history_initiative_id_nn not null,

  created timestamp constraint review_history_created_nn not null default now(),
  initiative_snapshot text,
  message varchar(1024),
  type reviewHistoryType constraint review_history_type_nn not null,

  constraint review_history_pk primary key (id),
  constraint review_history_initiative_id foreign key (initiative_id) references initiative(id),

  constraint review_row_valid check (((type = 'REVIEW_REJECT' or type = 'REVIEW_ACCEPT') and initiative_snapshot is null)
                                     or ((type = 'REVIEW_SENT') and initiative_snapshot is not null and message is null)
                                     or ((type = 'REVIEW_COMMENT' and initiative_snapshot is null and message is not null)))
);