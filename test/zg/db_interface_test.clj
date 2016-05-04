(ns zg.db-interface-test
  (:require [clojure.test :refer :all]
            [zg.db-interface :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

