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

(ns zg.db-interface
    "Namespace that contains interface to the database.")

(require '[clojure.java.jdbc       :as jdbc])

(require '[zg.db-spec     :as db-spec])
(require '[zg.format-date :as format-date])

(defn status->int
    [status]
    (if (= status :deleted)
        1
        0))

(defn dictionary-type->char
    [dictionary-type]
    (condp = dictionary-type
        :whitelist "w"
        :blacklist "b"))

(defn insert-word-into-dictionary
    "Try to insert new word into the dictionary. If insert fails, exception
     is thrown and needs to be caught outside this function."
    [word description user-name dictionary-type datetime]
    (jdbc/insert! db-spec/zg-db
        :dictionary {:word       word
                     :dictionary (dictionary-type->char dictionary-type)
                     :user       user-name
                     :datetime   datetime
                     :deleted     0
                     :description description}))

(defn add-new-word-into-dictionary
    ([word user-name dictionary-type]
    (let [datetime (format-date/format-current-date)]
        (println "storing" datetime word)
        (try
            (insert-word-into-dictionary word nil user-name dictionary-type datetime)
            (catch Exception e
                (println e)))))
    ([word description user-name dictionary-type]
    (let [datetime (format-date/format-current-date)]
        (println "storing" datetime word)
        (try
            (insert-word-into-dictionary word description user-name dictionary-type datetime)
            (catch Exception e
                (try
                    (jdbc/update! db-spec/zg-db
                          :dictionary {:description description} ["word=?" word])
                    (catch Exception e
                        (println e))))))))

(defn set-word-status
    [word dictionary-type status]
    (try
        (jdbc/update! db-spec/zg-db
                      :dictionary {:deleted (status->int status)} ["word = ? and dictionary = ?" word (dictionary-type->char dictionary-type)])
        (catch Exception e
            (println e)
            [])))

(defn delete-word
    "Mark the selected word with the flag 'deleted'. The word remains in the
     dictionary so it can be undeleted in the future."
    [word dictionary-type]
    (set-word-status word dictionary-type :deleted))

(defn undelete-word
    "Mark the selected word with the flag 'active' (ie it's 'undeleted')."
    [word dictionary-type]
    (set-word-status word dictionary-type :undeleted))

(defn read-all-words
    "Read all words from both dictionaries (dictionary-type is nil) or
     from selected dictionary (dictionary-type is specified)."
    [dictionary-type]
    (try
        (if dictionary-type
            (jdbc/query db-spec/zg-db
                ["select * from dictionary where dictionary=? order by word"
                    (dictionary-type->char dictionary-type)])
            (jdbc/query db-spec/zg-db
                ["select * from dictionary order by word"]))
        (catch Exception e
            (println e)
            [])))

(defn read-words-for-pattern
    "Read all words that are like specified pattern from both dictionaries
     (dictionary-type is nil) or from selected dictionary (dictionary-type is specified)."
    [pattern dictionary-type]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where word like ? and dictionary=? order by word"
                        (str "%" pattern "%") (dictionary-type->char dictionary-type)])
        (catch Exception e
            (println e)
            nil)))

(defn read-words-with-status
    "Read all words with specified status (deleted/active) from both dictionaries
     (dictionary-type is nil) or from selected dictionary (dictionary-type is specified)."
    [status dictionary-type]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where deleted = ? and dictionary=? order by word"
                        (status->int status) (dictionary-type->char dictionary-type)])
        (catch Exception e
            (println e)
            [])))

(defn read-deleted-words
    "Read all deleted words from both dictionaries (dictionary-type is nil) or
     from selected dictionary (dictionary-type is specified)."
    [dictionary-type]
    (read-words-with-status :deleted dictionary-type))

(defn read-active-words
    "Read all undeleted words from both dictionaries (dictionary-type is nil) or
     from selected dictionary (dictionary-type is specified)."
    [dictionary-type]
    (read-words-with-status :undeleted dictionary-type))

(defn read-changes-statistic
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select user, count(*) as cnt from dictionary group by user order by cnt desc"])
        (catch Exception e
            (println e)
            [])))

(defn read-changes
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary order by datetime"])
        (catch Exception e
            (println e)
            [])))

(defn read-changes-for-user
    [user-name]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where user=? order by datetime" user-name])
        (catch Exception e
            (println e)
            [])))

