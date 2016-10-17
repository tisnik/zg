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

(ns zg.utils-test
  (:require [clojure.test :refer :all]
            [zg.utils :refer :all]))

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

(deftest test-third-existence
    "Check that the zg.utils/third definition exists."
    (testing "if the zg.utils/third definition exists."
        (is (callable? 'zg.utils/third))))


(deftest test-substring-existence
    "Check that the zg.utils/substring definition exists."
    (testing "if the zg.utils/substring definition exists."
        (is (callable? 'zg.utils/substring))))


(deftest test-startsWith-existence
    "Check that the zg.utils/startsWith definition exists."
    (testing "if the zg.utils/startsWith definition exists."
        (is (callable? 'zg.utils/startsWith))))


(deftest test-endsWith-existence
    "Check that the zg.utils/endsWith definition exists."
    (testing "if the zg.utils/endsWith definition exists."
        (is (callable? 'zg.utils/endsWith))))


(deftest test-contains-existence
    "Check that the zg.utils/contains definition exists."
    (testing "if the zg.utils/contains definition exists."
        (is (callable? 'zg.utils/contains))))


(deftest test-replaceAll-existence
    "Check that the zg.utils/replaceAll definition exists."
    (testing "if the zg.utils/replaceAll definition exists."
        (is (callable? 'zg.utils/replaceAll))))


(deftest test-get-exception-message-existence
    "Check that the zg.utils/get-exception-message definition exists."
    (testing "if the zg.utils/get-exception-message definition exists."
        (is (callable? 'zg.utils/get-exception-message))))

;
; Tests for behaviour of all functions
;

(deftest test-get-exception-message-1
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (throw (new java.lang.Exception "Message text"))
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (= "Message text" (get-exception-message e)))))))

(deftest test-get-exception-message-2
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (/ 1 0)
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (= "Divide by zero" (get-exception-message e)))))))

(deftest test-get-exception-message-3
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (Integer/parseInt "unparseable")
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (.startsWith (get-exception-message e) "For input string:"))))))

(deftest test-get-exception-message-4
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (throw (new java.lang.Exception ""))
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (= "" (get-exception-message e)))))))

(deftest test-get-exception-message-5
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (throw (new java.lang.Exception))
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (nil? (get-exception-message e)))))))

(deftest test-get-exception-message-6
    "Check the function zg.utils/get-exception-message."
    (testing "the function zg.utils/get-exception-message."
        (try
            (println (nth [] 10)) ; realize the sequence and getter
            (is nil "Exception not thrown as expected!")
            (catch Exception e
                (is (nil? (get-exception-message e)))))))

(deftest test-third-1
    "Check the function zg.utils/third."
    (testing "the function zg.utils/third."
        (are [x y] (= x y)
            3 (third [1 2 3])
            3 (third [1 2 3 4 5])
            3 (third '(1 2 3))
            3 (third '(1 2 3 4 5)))))

(deftest test-third-2
    "Check the function zg.utils/third."
    (testing "the function zg.utils/third."
        (are [x y] (= x y)
            nil (third [])
            nil (third '())
            nil (third [1])
            nil (third '(1))
            nil (third [1 2])
            nil (third '(1 2)))))

(deftest test-third-not-NPE
    "Check the function zg.utils/third."
    (testing "the function zg.utils/third."
        (are [x y] (= x y)
            nil (third nil))))

(deftest test-substring-1
    "Check the function zg.utils/substring."
    (testing "the function zg.utils/substring."
        (are [x y] (= x y)
            "H"      (substring "Hello world!" 0 1)
            "He"     (substring "Hello world!" 0 2)
            "Hello"  (substring "Hello world!" 0 5)
            "Hello " (substring "Hello world!" 0 6))))

(deftest test-substring-2
    "Check the function zg.utils/substring."
    (testing "the function zg.utils/substring."
        (are [x y] (= x y)
            "w"      (substring "Hello world!" 6 7)
            "wo"     (substring "Hello world!" 6 8)
            "world"  (substring "Hello world!" 6 11)
            "world!" (substring "Hello world!" 6 12))))

(deftest test-substring-3
    "Check the function zg.utils/substring."
    (testing "the function zg.utils/substring."
        (are [x y] (= x y)
            "Hello world!" (substring "Hello world!" 0)
            "ello world!"  (substring "Hello world!" 1)
            "world!"       (substring "Hello world!" 6)
            "!"            (substring "Hello world!" 11)
            ""             (substring "Hello world!" 12))))

(deftest test-substring-empty-result
    "Check the function zg.utils/substring."
    (testing "the function zg.utils/substring."
        (are [x y] (= x y)
            "" (substring "Hello world!" 0 0)
            "" (substring "Hello world!" 1 1)
            "" (substring "Hello world!" 2 2)
            "" (substring "Hello world!" 10 10))))

(deftest test-substring-NPE
    "Check the function zg.utils/substring."
    (testing "the function zg.utils/substring."
        (is (thrown? NullPointerException (substring nil 0 0)))
        (is (thrown? NullPointerException (substring "" nil 0)))
        (is (thrown? NullPointerException (substring "" 0 nil)))))

(deftest test-contains-1
    "Check the function zg.utils/contains."
    (testing "the function zg.utils/contains."
        (are [x y] (= x y)
            false (contains "Hello world!" "h")
            true  (contains "Hello world!" "H")
            true  (contains "Hello world!" " ")
            true  (contains "Hello world!" "!"))))

(deftest test-contains-2
    "Check the function zg.utils/contains."
    (testing "the function zg.utils/contains."
        (are [x y] (= x y)
            false (contains "Hello world!" "hello")
            true  (contains "Hello world!" "Hello")
            true  (contains "Hello world!" "o w")
            true  (contains "Hello world!" "world!"))))

