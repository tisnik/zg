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

