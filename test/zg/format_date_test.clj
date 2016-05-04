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

