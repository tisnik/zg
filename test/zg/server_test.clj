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

(ns zg.server-test
  (:require [clojure.test :refer :all]
            [zg.server :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

;
; Tests for various functions existence
;

(deftest test-println-and-flush-existence
    "Check that the zg.server/println-and-flush definition exists."
    (testing "if the zg.server/println-and-flush definition exists."
        (is (callable? 'zg.server/println-and-flush))))


(deftest test-get-user-name-existence
    "Check that the zg.server/get-user-name definition exists."
    (testing "if the zg.server/get-user-name definition exists."
        (is (callable? 'zg.server/get-user-name))))


(deftest test-get-title-existence
    "Check that the zg.server/get-title definition exists."
    (testing "if the zg.server/get-title definition exists."
        (is (callable? 'zg.server/get-title))))


(deftest test-get-url-prefix-existence
    "Check that the zg.server/get-url-prefix definition exists."
    (testing "if the zg.server/get-url-prefix definition exists."
        (is (callable? 'zg.server/get-url-prefix))))


(deftest test-finish-processing-existence
    "Check that the zg.server/finish-processing definition exists."
    (testing "if the zg.server/finish-processing definition exists."
        (is (callable? 'zg.server/finish-processing))))


(deftest test-process-front-page-existence
    "Check that the zg.server/process-front-page definition exists."
    (testing "if the zg.server/process-front-page definition exists."
        (is (callable? 'zg.server/process-front-page))))


(deftest test-process-whitelist-existence
    "Check that the zg.server/process-whitelist definition exists."
    (testing "if the zg.server/process-whitelist definition exists."
        (is (callable? 'zg.server/process-whitelist))))


(deftest test-process-blacklist-existence
    "Check that the zg.server/process-blacklist definition exists."
    (testing "if the zg.server/process-blacklist definition exists."
        (is (callable? 'zg.server/process-blacklist))))


(deftest test-store-word-existence
    "Check that the zg.server/store-word definition exists."
    (testing "if the zg.server/store-word definition exists."
        (is (callable? 'zg.server/store-word))))


(deftest test-store-words-existence
    "Check that the zg.server/store-words definition exists."
    (testing "if the zg.server/store-words definition exists."
        (is (callable? 'zg.server/store-words))))


(deftest test-add-word-message-existence
    "Check that the zg.server/add-word-message definition exists."
    (testing "if the zg.server/add-word-message definition exists."
        (is (callable? 'zg.server/add-word-message))))


(deftest test-add-words-message-existence
    "Check that the zg.server/add-words-message definition exists."
    (testing "if the zg.server/add-words-message definition exists."
        (is (callable? 'zg.server/add-words-message))))


(deftest test-split-words-existence
    "Check that the zg.server/split-words definition exists."
    (testing "if the zg.server/split-words definition exists."
        (is (callable? 'zg.server/split-words))))


(deftest test-proper-word?-existence
    "Check that the zg.server/proper-word? definition exists."
    (testing "if the zg.server/proper-word? definition exists."
        (is (callable? 'zg.server/proper-word?))))


(deftest test-process-add-word-existence
    "Check that the zg.server/process-add-word definition exists."
    (testing "if the zg.server/process-add-word definition exists."
        (is (callable? 'zg.server/process-add-word))))


;
; Test for function behaviours
;

