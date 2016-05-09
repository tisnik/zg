;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns zg.format-date)

(def unified-date-format
    (new java.text.SimpleDateFormat "yyyy-MM-dd HH:mm:ss"))

(defn format-date
    [date]
    (.format unified-date-format date))

(defn format-current-date
    []
    (format-date (new java.util.Date)))

