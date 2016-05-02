#!/bin/sh

DATABASE=zg.db

sqlite3 ../${DATABASE} "select * from dictionary where deleted=1 order by word"

