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

(ns zg.dictionary-interface
    "Functions providing dictionary interface.")

(require '[clojure.tools.logging   :as log])

(require '[zg.db-interface         :as db-interface])

(defn proper-word-for-blacklist?
    [word]
    (re-matches #"[A-Za-z0-9.'\-\s_/]+" word))

(defn proper-word-for-whitelist?
    [word]
    (re-matches #"[A-Za-z0-9.'\-\s_]+" word))

(defn store-word
    "Store one word into the dictionary."
    [word correct-form description user-name dictionary-type]
    (log/info "About to store following word:" word " with description: " description " and/or correct form: " correct-form
             " into dictionary: " (str dictionary-type))
    (db-interface/add-new-word-into-dictionary word correct-form description (or user-name "(*unknown*)") dictionary-type))

(defn store-words
    "Store sequence of words into the dictionary."
    [words user-name dictionary-type]
    (log/info "About to store following words:" words)
    (doseq [word words]
        (db-interface/add-new-word-into-dictionary word user-name dictionary-type)))

