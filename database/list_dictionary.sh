#!/bin/sh

DATABASE=zg.db

sqlite3 ../${DATABASE} "select * from dictionary order by word"

