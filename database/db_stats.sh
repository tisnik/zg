#!/bin/sh

#
#  (C) Copyright 2016  Pavel Tisnovsky
#
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
#
#  Contributors:
#      Pavel Tisnovsky
#

DATABASE=zg.db

function show_stat()
{
    echo "   active:  `sqlite3 ../${DATABASE} \"select count(*) from dictionary where deleted=0 and dictionary='$1'\"`"
    echo "   deleted: `sqlite3 ../${DATABASE} \"select count(*) from dictionary where deleted=1 and dictionary='$1'\"`"
    echo "   all:     `sqlite3 ../${DATABASE} \"select count(*) from dictionary where               dictionary='$1'\"`"
}

echo "Whitelist"
show_stat 'w'

echo "Blacklist"
show_stat 'b'

echo "Atomic typos"
show_stat 'a'

echo "Universal"
show_stat 'u'

