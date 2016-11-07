;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns zg.format-date
    "Functions for working with dates and datetimes.")

(def unified-date-format
    "Object with the specification how date should be formatted."
    (new java.text.SimpleDateFormat "yyyy-MM-dd HH:mm:ss"))

(defn format-date
    "Format given date using unified date format."
    [^java.util.Date date]
    (let [^java.text.DateFormat format-object unified-date-format]
        (.format format-object date)))

(defn format-current-date
    "Format current date using unified date format."
    []
    (format-date (new java.util.Date)))

