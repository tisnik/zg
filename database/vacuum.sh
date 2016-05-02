#!/bin/sh

DATABASE=zg.db

sqlite3 ../${DATABASE} "vacuum"

