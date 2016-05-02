#!/bin/sh

DATABASE=zg.db

sqlite3 ../${DATABASE} "select * from dictionary where deleted=0 order by word"

