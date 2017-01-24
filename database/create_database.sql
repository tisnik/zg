create table sources (
    id          integer primary key asc,
    source      text not null
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
    primary key (word, dictionary)
);

