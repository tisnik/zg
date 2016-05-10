#!/bin/sh

DATABASE=zg.db

cat database_dump.sql | sqlite3 ../${DATABASE}

