#!/bin/sh

DATABASE=zg.db

cat drop_database.sql | sqlite3 ../${DATABASE}

