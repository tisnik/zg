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

