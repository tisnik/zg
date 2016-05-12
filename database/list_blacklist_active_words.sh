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

sqlite3 ../${DATABASE} "select * from dictionary where deleted=0 and dictionary='b' order by word"

