#!/bin/sh

DATABASE=zg.db

cat create_database.sql | sqlite3 ../${DATABASE}

