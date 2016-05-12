create table dictionary (
    word        text primary key,
    dictionary  char, -- 'w':whitelist  'b': blacklist
    user        text,
    datetime    text,
    deleted     integer,
    description text
);

