;
;  (C) Copyright 2016, 2020  Pavel Tisnovsky
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

(defn process-use-it
  [use-it]
  (condp = use-it
      "Yes" 1
      "No"  0
            2))

(defn process-class
  [class]
  (condp = (.toLowerCase class)
    "N/A" nil
    "n/a" nil
    "verb" 1
    "noun" 2
    "adverb" 3
    "adjective" 4
    "pronoun" 5
    "conjunction" 6
    "preposition" 7
    "interjection" 8
    "article" 9
    "numeral" 10
    "determiner" 11
    "exclamation" 12))

(defn store-word
  "Store one word into the dictionary."
  ([word correct-form description user-name dictionary-type]
   (log/info "About to store following word:" word
             " with description: " description
             " and/or correct form: " correct-form
             " into dictionary: " (str dictionary-type))
   (db-interface/add-new-word-into-dictionary word
                                              correct-form
                                              description
                                              (or user-name "(*unknown*)")
                                              dictionary-type))
  ([word description class use-it internal copyright source correct-forms incorrect-forms user-name dictionary-type]
   (log/info "About to store following word:" word
             " with description: " description
             " into dictionary: " (str dictionary-type))
   (db-interface/add-new-word-into-dictionary word
                                              description
                                              (process-class class)
                                              (process-use-it use-it)
                                              internal
                                              copyright
                                              (inc source)
                                              correct-forms
                                              incorrect-forms
                                              (or user-name "(*unknown*)")
                                              dictionary-type)))

(defn store-words
  "Store sequence of words into the dictionary."
  [words user-name dictionary-type]
  (log/info "About to store following words:" words)
  (doseq [word words]
    (db-interface/add-new-word-into-dictionary word user-name dictionary-type)))

