drop type if exists infoTextCategory;
create type infoTextCategory as enum ('MAIN', 'KANSALAISALOITE', 'KANSALAISALOITE_FI', 'TIEDOTTEET');

create table info_text (
    id bigserial,
    languageCode languageCode constraint infoText_languageCode_nn not null,
    category infoTextCategory constraint infoText_infoTextCategory_nn not null,
    footer_display boolean constraint infoText_footerDisplay_nn not null default false,
    uri varchar(100),
    published_subject varchar(100),
    draft_subject varchar(100),
    published text,
    draft text,
    modifier varchar(100),
    modified timestamp,

    orderPosition integer constraint infoText_orderPosition_nn not null,

    constraint infoText_pk primary key (id),
    constraint infoText_uri_u unique (uri),
    constraint infoText_orderPosition_u unique (languageCode, category, orderPosition)
);

insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'TIEDOTTEET', 'tiedotteet', 'Tiedotteet', 'Tiedotteet', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 100);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'TIEDOTTEET', 'aktuellt', 'Aktuellt', 'Aktuellt', 'Innehållet uppdateras', 'Innehållet uppdateras', 100);


insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'MAIN', 'ohje-vastuuhenkilolle', 'Ohje aloitteen vastuuhenkilölle', 'Ohje aloitteen vastuuhenkilölle', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 200);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'MAIN', 'anvisningar-for-ansvarspersoner', 'Anvisningar för ansvarspersoner', 'Anvisningar för ansvarspersoner', 'Innehållet uppdateras', 'Innehållet uppdateras', 200);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'MAIN', 'ohje-kannattajalle', '', 'Ohje aloitteen kannattajalle', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 300);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'MAIN', 'anvisningar-for-dem-som-vill-understoda-initiativ', '', 'Anvisningar för dem som vill understöda initiativ', 'Innehållet uppdateras', 'Innehållet uppdateras', 300);

insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE_FI', 'palvelun-tarkoitus', '', 'Palvelun tarkoitus', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 400);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE_FI', 'syftet-med-webbtjansten', '', 'Syftet med webbtjänsten', 'Innehållet uppdateras', 'Innehållet uppdateras', 400);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE_FI', 'palvelun-kayttoehdot', '', 'Palvelun käyttöehdot', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 500);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE_FI', 'anvandarvillkor-for-webbtjansten', '', 'Användarvillkor för webbtjänsten', 'Innehållet uppdateras', 'Innehållet uppdateras', 500);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('FI', 'KANSALAISALOITE_FI', 'henkilotietojen-suoja-ja-tietoturva', '', 'Henkilötietojen suoja ja tietoturva', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 600, true);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('SV', 'KANSALAISALOITE_FI', 'skydd-av-personuppgifter-och-dataskydd', '', 'Skydd av personuppgifter och dataskydd', 'Innehållet uppdateras', 'Innehållet uppdateras', 600, true);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE_FI', 'palvelun-avoin-kehittaminen', '', 'Palvelun avoin kehittäminen', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 700);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE_FI', 'oppen-utveckling-av-webbtjansten', '', 'Öppen utveckling av webbtjänsten', 'Innehållet uppdateras', 'Innehållet uppdateras', 700);

insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE_FI', 'viestintaviraston-hyvaksynta', '', 'Viestintäviraston hyväksyntä', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 750);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE_FI', 'kommunikationsverkets-godkannande', '', 'Kommunikationsverkets godkännande', 'Innehållet uppdateras', 'Innehållet uppdateras', 750);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('FI', 'KANSALAISALOITE_FI', 'yhteystiedot', '', 'Yhteystiedot', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 800, true);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('SV', 'KANSALAISALOITE_FI', 'kontaktuppgifter', '', 'Kontaktuppgifter', 'Innehållet uppdateras', 'Innehållet uppdateras', 800, true);


insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE', 'kansalaisaloite-lyhyesti', '', 'Kansalaisaloite lyhyesti', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 900);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE', 'medborgarinitiativet-i-korthet', '', 'Medborgarinitiativet i korthet', 'Innehållet uppdateras', 'Innehållet uppdateras', 900);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE', 'aloitteen-vaiheet', '', 'Aloitteen vaiheet', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 1000);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE', 'initiativets-skeden', '', 'Initiativets skeden', 'Innehållet uppdateras', 'Innehållet uppdateras', 1000);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE', 'ukk', '', 'Usein kysyttyjä kysymyksiä', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 1100);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE', 'vanliga-fragor', '', 'Vanliga frågor', 'Innehållet uppdateras', 'Innehållet uppdateras', 1100);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE', 'muita-aloitemuotoja', '', 'Muita aloitemuotoja', 'Sisältöä päivitetään paraikaa', 'Sisältöä päivitetään paraikaa', 1200);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE', 'andra-former-av-initiativ', '', 'Andra former av initiativ', 'Innehållet uppdateras', 'Innehållet uppdateras', 1200);

insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('FI', 'KANSALAISALOITE', 'muualla-verkossa', '', 'Muualla verkossa', 'Sisältöä päivitetään paraikaa','Sisältöä päivitetään paraikaa', 1250);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition) values ('SV', 'KANSALAISALOITE', 'information-pa-webben', '', 'Information på webben', 'Innehållet uppdateras', 'Innehållet uppdateras', 1250);

insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('FI', 'KANSALAISALOITE', 'briefly-in-english', '', 'Briefly in english', 'The content is currently being updated', 'The content is currently being updated', 1300, true);
insert into info_text (languageCode, category, uri, published_subject, draft_subject, published, draft, orderPosition, footer_display) values ('SV', 'KANSALAISALOITE', 'briefly_in_english', '', 'Briefly in english', 'The content is currently being updated', 'The content is currently being updated', 1300, true);

insert into schema_version (script) values ('02_infotext.sql');