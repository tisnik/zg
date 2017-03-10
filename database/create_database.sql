create table sources (
    id              integer primary key asc,
    source          text not null
);

create table classes (
    id              integer primary key asc,
    class           text not null
);

create table dictionary (
    word        text,
    dictionary  char, -- 'w':whitelist  'b': blacklist   'a':atomic typos
    user        text,
    datetime    text,
    deleted     integer,
    description text,
    correct     text, -- used by atomic typos only
    class       text,
    use         integer,
    internal    integer,
    copyright   integer,
    source      integer,
    primary key (word, dictionary)
    foreign key (source) references sources(id)
);

insert into classes (class) values("verb");
insert into classes (class) values("noun");
insert into classes (class) values("adverb");
insert into classes (class) values("adjective");
insert into classes (class) values("pronoun");
insert into classes (class) values("conjunction");
insert into classes (class) values("preposition");
insert into classes (class) values("interjection");
insert into classes (class) values("article");
insert into classes (class) values("numeral");
insert into classes (class) values("determiner");
insert into classes (class) values("exclamation");

