#!/bin/sh

DATABASE=zg.db

cat dump_database.sql | sqlite3 ../${DATABASE}

