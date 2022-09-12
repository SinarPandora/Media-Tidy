create table setting
(
    id         integer primary key autoincrement,
    key        text    not null unique,
    value      text    not null,
    value_type text    not null,
    visible    integer not null
);

create table profile
(
    id     integer primary key autoincrement,
    name   text    not null,
    active integer not null default true
);

create table source_folder
(
    id         integer primary key autoincrement,
    filepath   text    not null,
    alias      text    not null default '',
    update_at  integer not null default current_timestamp,
    active     integer not null default 1,
    profile_id integer not null
);

create unique index source_folder_path_profile_id_uindex
    on source_folder (filepath, profile_id);

create table tag
(
    id          integer primary key autoincrement,
    name        text    not null,
    description text    not null default '',
    folder      text    not null,
    not_index   integer not null default false,
    update_at   integer not null default current_timestamp,
    profile_id  integer not null,
    menu_order  integer not null default 0
);

create unique index tag_name_profile_id_uindex
    on tag (name, profile_id);

create table tidy_history
(
    id               integer primary key autoincrement,
    file_id          integer not null,
    origin_tag_id    integer,
    origin_source_id integer,
    origin_folder    text,
    origin_filename  text,
    tidied_tag_id    integer,
    tidied_folder    text,
    tidied_filename  text,
    create_at        integer not null default current_timestamp,
    action           text    not null,
    revert           integer not null default false
);

create table media_file
(
    id        integer primary key autoincrement,
    filename  text not null,
    folder    text not null,
    source_id integer,
    tag_id    integer
);
