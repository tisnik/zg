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

