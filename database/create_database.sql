create table dictionary (
    word        text primary key,
    dictionary  char, -- 'w':whitelist  'b': blacklist   'a':atomic typos
    user        text,
    datetime    text,
    deleted     integer,
    description text,
    correct     text  -- used by atomic typos only
);

