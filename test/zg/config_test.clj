(ns zg.config-test
  (:require [clojure.test :refer :all]
            [zg.config :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

;
; Tests for various functions
;

(deftest test-parse-int-existence
    "Check that the zg.config/parse-int definition exists."
    (testing "if the zg.config/parse-int definition exists."
        (is (callable? 'zg.config/parse-int))))

(deftest test-parse-float-existence
    "Check that the zg.config/parse-float definition exists."
    (testing "if the zg.config/parse-float definition exists."
        (is (callable? 'zg.config/parse-float))))

(deftest test-load-configuration-existence
    "Check that the zg.config/load-configuration definition exists."
    (testing "if the zg.config/load-configuration definition exists."
        (is (callable? 'zg.config/load-configuration))))

(deftest test-print-configuration-existence
    "Check that the zg.config/print-configuration definition exists."
    (testing "if the zg.config/print-configuration definition exists."
        (is (callable? 'zg.config/print-configuration))))

;
; Test for function behaviours
;

(deftest test-parse-int-zero
    "Check the behaviour of function zg.config/parse int."
    (are [x y] (= x y)
        0 (parse-int "0")
        0 (parse-int "00")
        0 (parse-int "000")
        0 (parse-int "-0")
        0 (parse-int "-00")
        0 (parse-int "-000")))

(deftest test-parse-int-positive-int
    "Check the behaviour of function zg.config/parse int."
    (are [x y] (= x y)
        1          (parse-int "1")
        2          (parse-int "2")
        42         (parse-int "42")
        65535      (parse-int "65535")
        65536      (parse-int "65536")
        2147483646 (parse-int "2147483646")))

(deftest test-parse-int-negative-int
    "Check the behaviour of function zg.config/parse int."
    (are [x y] (= x y)
        -1          (parse-int "-1")
        -2          (parse-int "-2")
        -42         (parse-int "-42")
        -65535      (parse-int "-65535")
        -65536      (parse-int "-65536")
        -2147483647 (parse-int "-2147483647")))

