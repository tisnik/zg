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

(ns zg.format-date-test
  (:require [clojure.test :refer :all]
            [zg.format-date :refer :all]))

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

(deftest test-format-date-existence
    "Check that the zg.format-date/format-date definition exists."
    (testing "if the zg.format-date/format-date definition exists."
        (is (callable? 'zg.format-date/format-date))))


(deftest test-format-current-date-existence
    "Check that the zg.format-date/format-current-date definition exists."
    (testing "if the zg.format-date/format-current-date definition exists."
        (is (callable? 'zg.format-date/format-current-date))))

;
; Test function behaviours
;

(deftest test-format-date-1
    "Check the function zg.format-date/format-date"
    (testing "the function zg.format-date/format-date." 
        (let [date (new java.util.Date 116 01 01 12 00)]
            (is (not (nil? (format-date date))))
            (is (= (type (format-date date)) java.lang.String))
            (is (= (count (format-date date)) 19)))))

(deftest test-format-date-2
    "Check the function zg.format-date/format-date"
    (testing "the function zg.format-date/format-date." 
        (let [date (new java.util.Date 116 01 01 12 00)]
            (is (= "2016-02-01 12:00:00" (format-date date))))
        (let [date (new java.util.Date 100 01 01 12 00)]
            (is (= "2000-02-01 12:00:00" (format-date date))))
        (let [date (new java.util.Date 0 00 01 00 00)]
            (is (= "1900-01-01 00:00:00" (format-date date))))))

(deftest test-format-current-date
    "Check the function zg.format-date/format-current-date"
    (testing "the function zg.format-date/format-current-date." 
        (is (not (nil? (format-current-date))))
        (is (= (type (format-current-date))))
        (is (= (count (format-current-date)) 19))))

