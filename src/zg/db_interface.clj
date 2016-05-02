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

(defn add-new-word-into-dictionary
    [word user-name]
    (let [datetime (format-date/format-current-date)]
        (println "storing" datetime word)
        (try
            (jdbc/insert! db-spec/zg-db
                          :dictionary {:word word :user user-name :datetime datetime :deleted 0})
            (catch Exception e
                (println e)))))

(defn status->int
    [status]
    (if (= status :deleted)
        1
        0))

(defn set-word-status
    [word status]
    (try
        (jdbc/update! db-spec/zg-db
                      :dictionary {:deleted (status->int status)} ["word = ?" word])
        (catch Exception e
            (println e)
            [])))

(defn delete-word
    [word]
    (set-word-status word :deleted))

(defn undelete-word
    [word]
    (set-word-status word :undeleted))

(defn read-all-words
    []
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary order by word"])
        (catch Exception e
            (println e)
            [])))

(defn read-words-for-pattern
    [pattern]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where word like ? order by word" (str "%" pattern "%")])
        (catch Exception e
            (println e)
            nil)))

(defn read-words-with-status
    [status]
    (try
        (jdbc/query db-spec/zg-db
                        ["select * from dictionary where deleted = ? order by word" (status->int status)])
        (catch Exception e
            (println e)
            [])))

(defn read-deleted-words
    []
    (read-words-with-status :deleted))

(defn read-active-words
    []
    (read-words-with-status :undeleted))

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
