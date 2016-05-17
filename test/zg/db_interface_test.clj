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

;
; Tests for function existence
;

(deftest test-status->int-existence
    "Check that the zg.db-interface/status->int definition exists."
    (testing "if the zg.db-interface/status->int definition exists."
        (is (callable? 'zg.db-interface/status->int))))


(deftest test-dictionary-type->char-existence
    "Check that the zg.db-interface/dictionary-type->char definition exists."
    (testing "if the zg.db-interface/dictionary-type->char definition exists."
        (is (callable? 'zg.db-interface/dictionary-type->char))))


(deftest test-insert-word-into-dictionary-existence
    "Check that the zg.db-interface/insert-word-into-dictionary definition exists."
    (testing "if the zg.db-interface/insert-word-into-dictionary definition exists."
        (is (callable? 'zg.db-interface/insert-word-into-dictionary))))


(deftest test-add-new-word-into-dictionary-existence
    "Check that the zg.db-interface/add-new-word-into-dictionary definition exists."
    (testing "if the zg.db-interface/add-new-word-into-dictionary definition exists."
        (is (callable? 'zg.db-interface/add-new-word-into-dictionary))))


