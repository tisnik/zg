;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
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

