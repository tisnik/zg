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

(ns zg.db-interface-test
  (:require [clojure.test      :refer :all]
            [clojure.java.jdbc :as jdbc]
            [zg.db-interface   :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

;
; Tests for function existence
;

(deftest test-status->int-existence
    "Check that the zg.db-interface/status->int definition exists."
    (testing "if the zg.db-interface/status->int definition exists."
        (is (callable? 'zg.db-interface/status->int))))


(deftest test-dictionary-type->char-existence
    "Check that the zg.db-interface/dictionary-type->char definition exists."
    (testing "if the zg.db-interface/dictionary-type->char definition exists."
        (is (callable? 'zg.db-interface/dictionary-type->char))))


(deftest test-insert-word-into-dictionary-existence
    "Check that the zg.db-interface/insert-word-into-dictionary definition exists."
    (testing "if the zg.db-interface/insert-word-into-dictionary definition exists."
        (is (callable? 'zg.db-interface/insert-word-into-dictionary))))


(deftest test-add-new-word-into-dictionary-existence
    "Check that the zg.db-interface/add-new-word-into-dictionary definition exists."
    (testing "if the zg.db-interface/add-new-word-into-dictionary definition exists."
        (is (callable? 'zg.db-interface/add-new-word-into-dictionary))))


(deftest test-set-word-status-existence
    "Check that the zg.db-interface/set-word-status definition exists."
    (testing "if the zg.db-interface/set-word-status definition exists."
        (is (callable? 'zg.db-interface/set-word-status))))


(deftest test-delete-word-existence
    "Check that the zg.db-interface/delete-word definition exists."
    (testing "if the zg.db-interface/delete-word definition exists."
        (is (callable? 'zg.db-interface/delete-word))))


(deftest test-undelete-word-existence
    "Check that the zg.db-interface/undelete-word definition exists."
    (testing "if the zg.db-interface/undelete-word definition exists."
        (is (callable? 'zg.db-interface/undelete-word))))


(deftest test-read-all-words-existence
    "Check that the zg.db-interface/read-all-words definition exists."
    (testing "if the zg.db-interface/read-all-words definition exists."
        (is (callable? 'zg.db-interface/read-all-words))))


(deftest test-read-words-for-pattern-existence
    "Check that the zg.db-interface/read-words-for-pattern definition exists."
    (testing "if the zg.db-interface/read-words-for-pattern definition exists."
        (is (callable? 'zg.db-interface/read-words-for-pattern))))


(deftest test-read-words-with-status-existence
    "Check that the zg.db-interface/read-words-with-status definition exists."
    (testing "if the zg.db-interface/read-words-with-status definition exists."
        (is (callable? 'zg.db-interface/read-words-with-status))))


(deftest test-read-deleted-words-existence
    "Check that the zg.db-interface/read-deleted-words definition exists."
    (testing "if the zg.db-interface/read-deleted-words definition exists."
        (is (callable? 'zg.db-interface/read-deleted-words))))


(deftest test-read-active-words-existence
    "Check that the zg.db-interface/read-active-words definition exists."
    (testing "if the zg.db-interface/read-active-words definition exists."
        (is (callable? 'zg.db-interface/read-active-words))))


(deftest test-read-changes-statistic-existence
    "Check that the zg.db-interface/read-changes-statistic definition exists."
    (testing "if the zg.db-interface/read-changes-statistic definition exists."
        (is (callable? 'zg.db-interface/read-changes-statistic))))


(deftest test-read-changes-existence
    "Check that the zg.db-interface/read-changes definition exists."
    (testing "if the zg.db-interface/read-changes definition exists."
        (is (callable? 'zg.db-interface/read-changes))))


(deftest test-read-changes-for-user-existence
    "Check that the zg.db-interface/read-changes-for-user definition exists."
    (testing "if the zg.db-interface/read-changes-for-user definition exists."
        (is (callable? 'zg.db-interface/read-changes-for-user))))

;
; Test function behaviour
;

(deftest test-status->int
    "Checking the function status->int."
    (testing "the function status->int."
        (are [x y] (= x (status->int y))
            1 :deleted
            0 :undeleted
            0 nil)))

(deftest test-dictionary-type->char
    "Checking the function dictionary-type->char."
    (testing "the function dictionary-type->char."
        (are [x y] (= x (dictionary-type->char y))
            "w" :whitelist
            "b" :blacklist
            "o" :other)))

(deftest test-insert-word-into-dictionary
    "Checking the function insert-word-into-dictionary."
    (testing "the function insert-word-into-dictionary."
        (with-redefs [jdbc/insert! (fn [db-spec table-name data-entry] table-name)]
            (are [x y] (= x y)
                :dictionary (insert-word-into-dictionary "word"           nil "description" "user-name"           :whitelist "2016-01-01")
                :dictionary (insert-word-into-dictionary "word"           nil "description" "user-name"           :blacklist "2016-01-01")
                :dictionary (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                :dictionary (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                :dictionary (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")
                :dictionary (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")))
        (with-redefs [jdbc/insert! (fn [db-spec table-name data-entry] (get data-entry :word))]
            (are [x y] (= x y)
                "word"           (insert-word-into-dictionary "word"           nil "description" "user-name"           :whitelist "2016-01-01")
                "word"           (insert-word-into-dictionary "word"           nil "description" "user-name"           :blacklist "2016-01-01")
                "different word" (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                "different word" (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                "different word" (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")
                "different word" (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")))
        (with-redefs [jdbc/insert! (fn [db-spec table-name data-entry] (get data-entry :dictionary))]
            (are [x y] (= x y)
                "w" (insert-word-into-dictionary "word"           nil "description" "user-name"           :whitelist "2016-01-01")
                "b" (insert-word-into-dictionary "word"           nil "description" "user-name"           :blacklist "2016-01-01")
                "b" (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                "b" (insert-word-into-dictionary "different word" nil "description" "user-name"           :blacklist "2016-01-01")
                "b" (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")
                "b" (insert-word-into-dictionary "different word" nil "description" "different user-name" :blacklist "2016-01-01")))))

