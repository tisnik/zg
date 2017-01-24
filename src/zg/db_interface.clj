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

(ns zg.db-interface
    "Namespace that contains interface to the database.")

(require '[clojure.java.jdbc     :as jdbc])
(require '[clojure.tools.logging :as log])

(require '[zg.db-spec     :as db-spec])
(require '[zg.format-date :as format-date])

(defn status->int
    "Convert word status (:deleted, :undeleted) into an integer."
    [status]
    (if (= status :deleted)
        1
        0))

(defn dictionary-type->char
    "Convert dictionary type (:whitelist, :blacklist) into a one character."
    [dictionary-type]
    (condp = dictionary-type
        :whitelist    "w"
        :blacklist    "b"
        :atomic-typos "a"
                      "o"))

(defn insert-word-into-dictionary
    "Try to insert new word into the dictionary. If insert fails, exception
     is thrown and it needs to be caught outside this function."
    [word correct-form description user-name dictionary-type datetime]
    (jdbc/insert! db-spec/zg-db
        :dictionary {:word       word
                     :correct    correct-form
                     :dictionary (dictionary-type->char dictionary-type)
                     :user       user-name
                     :datetime   datetime
                     :deleted     0
                     :description description}))

(defn add-new-word-into-dictionary
    "Add new word into the dictionary. If the word already exist, it is 
     simply updated (make sense only for blacklist)."
    ([word user-name dictionary-type]
    (let [datetime (format-date/format-current-date)]
        (log/info "storing" datetime word)
        (try
            (insert-word-into-dictionary word nil nil user-name dictionary-type datetime)
            (catch Exception e
                (log/error e "insert word into dictionary")))))
    ([word description user-name dictionary-type]
    (let [datetime (format-date/format-current-date)]
        (log/info "storing" datetime word)
        (try
            (insert-word-into-dictionary word nil description user-name dictionary-type datetime)
            (catch Exception e
                (try
                    (jdbc/update! db-spec/zg-db
                          :dictionary {:description description} ["word=?" word])
                    (catch Exception e
                        (log/error e "insert word into dictionary")))))))
    ([word correct-form description user-name dictionary-type]
    (let [datetime (format-date/format-current-date)]
        (log/info "storing" datetime word)
        (try
            (insert-word-into-dictionary word correct-form description user-name dictionary-type datetime)
            (catch Exception e
                (try
                    (jdbc/update! db-spec/zg-db
                          :dictionary {:description description} ["word=?" word])
                    (catch Exception e
                        (log/error e "insert word into dictionary"))))))))

(defn set-word-status
    "Set or change status of given word in the dictionary (:deleted, :undeleted)."
    [word dictionary-type status]
    (try
        (jdbc/update! db-spec/zg-db
                      :dictionary {:deleted (status->int status)}
                                   ["word = ? and dictionary = ?" word (dictionary-type->char dictionary-type)])
        (catch Exception e
            (log/error e "set/update word status")
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
            (log/error e "read all words")
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
            (log/error e "read words for given pattern" pattern)
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
            (log/error e "read words with given status" status)
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
    "Read basic statistic about changes made by all users. For each user, number of changes is displayed."
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select user, count(*) as cnt from dictionary group by user order by cnt desc"])
        (catch Exception e
            (log/error e "read changes statistic")
            [])))

(defn read-changes
    "Read all changes sorted by date and time the change has been made."
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary order by datetime"])
        (catch Exception e
            (log/error e "read changes")
            [])))

(defn read-changes-for-user
    "Read all changes for selected user sorted by date and time the change has been made."
    [user-name]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where user=? order by datetime" user-name])
        (catch Exception e
            (log/error e "read changes for user" user-name)
            [])))

(defn read-sources
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select id, source from dictionary order by source"])
        (catch Exception e
            (log/error e "read sources")
            [])))

