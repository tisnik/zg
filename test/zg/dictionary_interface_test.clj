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

(ns zg.dictionary-interface-test
  (:require [clojure.test :refer :all]
            [zg.dictionary-interface :refer :all]))

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

(deftest test-proper-word-for-whitelist?-existence
    "Check that the zg.dictionary-interface/proper-word-for-whitelist? definition exists."
    (testing "if the zg.dictionary-interface/proper-word-for-whitelist? definition exists."
        (is (callable? 'zg.dictionary-interface/proper-word-for-whitelist?))))


(deftest test-proper-word-for-blacklist?-existence
    "Check that the zg.dictionary-interface/proper-word-for-blacklist? definition exists."
    (testing "if the zg.dictionary-interface/proper-word-for-blacklist? definition exists."
        (is (callable? 'zg.dictionary-interface/proper-word-for-blacklist?))))


(deftest test-store-word-existence
    "Check that the zg.dictionary-interface/store-word definition exists."
    (testing "if the zg.dictionary-interface/store-word definition exists."
        (is (callable? 'zg.dictionary-interface/store-word))))


(deftest test-store-words-existence
    "Check that the zg.dictionary-interface/store-words definition exists."
    (testing "if the zg.dictionary-interface/store-words definition exists."
        (is (callable? 'zg.dictionary-interface/store-words))))


(deftest test-proper-word-for-blacklist?
    "Check the function zg.dictionary-interface/proper-word-for-blacklist?"
    (testing "the function zg.dictionary-interface/proper-word-for-blacklist?"
        (are [x] (seq (proper-word-for-blacklist? x))
            "test"
            "Test"
            "TEST"
            "Test0"
            "0"
            "I'm"
            "This is test"
            "This is test."
            "/"
            "a/b"
            "foo/bar")))

(deftest test-proper-word-for-blacklist?-negative
    "Check the function zg.dictionary-interface/proper-word-for-blacklist?"
    (testing "the function zg.dictionary-interface/proper-word-for-blacklist?"
        (are [x] (not (seq (proper-word-for-blacklist? x)))
            ""
            "!"
            "!!"
            "! !"
            "Test!"
            "This is test!")))

(deftest test-proper-word-for-whitelist?
    "Check the function zg.dictionary-interface/proper-word-for-whitelist?"
    (testing "the function zg.dictionary-interface/proper-word-for-whitelist?"
        (are [x] (seq (proper-word-for-whitelist? x))
            "test"
            "Test"
            "TEST"
            "Test0"
            "0"
            "I'm"
            "This is test"
            "This is test.")))

(deftest test-proper-word-for-whitelist?-negative
    "Check the function zg.dictionary-interface/proper-word-for-whitelist?"
    (testing "the function zg.dictionary-interface/proper-word-for-whitelist?"
        (are [x] (not (seq (proper-word-for-whitelist? x)))
            ""
            "!"
            "!!"
            "! !"
            "Test!"
            "This is test!"
            "/"
            "a/b"
            "foo/bar")))

