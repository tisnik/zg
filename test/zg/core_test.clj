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

(ns zg.core-test
  (:require [clojure.test       :refer :all]
            [zg.core            :refer :all]
            [ring.adapter.jetty :as    jetty]
            [clojure.tools.cli  :as    cli]))

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

(deftest test-start-server-existence
  "Check that the zg.core/start-server definition exists."
  (testing "if the zg.core/start-server definition exists."
           (is (callable? 'zg.core/start-server))))


(deftest test-get-and-check-port-existence
  "Check that the zg.core/get-and-check-port definition exists."
  (testing "if the zg.core/get-and-check-port definition exists."
           (is (callable? 'zg.core/get-and-check-port))))


(deftest test-get-port-existence
  "Check that the zg.core/get-port definition exists."
  (testing "if the zg.core/get-port definition exists."
           (is (callable? 'zg.core/get-port))))


(deftest test--main-existence
  "Check that the zg.core/-main definition exists."
  (testing "if the zg.core/-main definition exists."
           (is (callable? 'zg.core/-main))))

;
; Tests for function behaviours
;

(deftest test-get-port
  "Check the function zg.core/get-port."
  (testing "the function zg.core/get-port."
           (is (= (get-port "1")     "1"))
           (is (= (get-port "2")     "2"))
           (is (= (get-port "3000")  "3000"))
           (is (= (get-port "65534") "65534"))
           (is (= (get-port "65535") "65535"))))

(deftest test-get-port-special-cases
  "Check the function zg.core/get-port."
  (testing "the function zg.core/get-port."
           (is (= (get-port nil)     "3000"))
           (is (= (get-port "")      "3000"))
           (is (= (get-port 0)       "3000"))
           (is (= (get-port 1)       "3000"))
           (is (= (get-port 65535)   "3000"))
           (is (= (get-port 65536)   "3000"))))

(deftest test-get-port-negative
  "Check the function zg.core/get-port."
  (testing "the function zg.core/get-port."
           (is (thrown? AssertionError (get-port "0")))
           (is (thrown? AssertionError (get-port "-1")))
           (is (thrown? AssertionError (get-port "-2")))
           (is (thrown? AssertionError (get-port "65536")))
           (is (thrown? AssertionError (get-port "65537")))
           (is (thrown? AssertionError (get-port "1000000")))))

(deftest test-get-and-check-port
  "Check the function zg.core/get-and-check-port."
  (testing "the function zg.core/get-and-check-port."
           (is (= (get-and-check-port "1")     "1"))
           (is (= (get-and-check-port "2")     "2"))
           (is (= (get-and-check-port "65534") "65534"))
           (is (= (get-and-check-port "65535") "65535"))))

(deftest test-get-and-check-port-negative
  "Check the function zg.core/get-and-check-port."
  (testing "the function zg.core/get-and-check-port."
           (is (thrown? AssertionError (get-and-check-port "-1")))
           (is (thrown? AssertionError (get-and-check-port "0")))
           (is (thrown? AssertionError (get-and-check-port "65536")))
           (is (thrown? AssertionError (get-and-check-port "65537")))
           (is (thrown? AssertionError (get-and-check-port "1000000")))))

(deftest test-start-server-positive-1
  (testing "zg.core/start-server"
           ; use mock instead of jetty/run-jetty
           (with-redefs [jetty/run-jetty (fn [app port] port)]
             (is (= {:port 1}     (start-server "1")))
             (is (= {:port 2}     (start-server "2")))
             (is (= {:port 1000}  (start-server "1000")))
             (is (= {:port 65534} (start-server "65534")))
             (is (= {:port 65535} (start-server "65535"))))))

(deftest test-start-server-positive-2
  (testing "zg.core/start-server"
           ; use mock instead of jetty/run-jetty
           (with-redefs [jetty/run-jetty (fn [app port] app)]
             (is (= app (start-server "1")))
             (is (= app (start-server "2")))
             (is (= app (start-server "1000")))
             (is (= app (start-server "65534")))
             (is (= app (start-server "65535"))))))

(deftest test-start-server-wrong-port-number
  (testing "zg.core/start-server"
           ; use mock instead of jetty/run-jetty
           (with-redefs [jetty/run-jetty (fn [app port] port)]
             (is (= {:port -1}      (start-server "-1")))
             (is (= {:port 0}       (start-server "0")))
             (is (= {:port 65536}   (start-server "65536")))
             (is (= {:port 65537}   (start-server "65537")))
             (is (= {:port 1000000} (start-server "1000000"))))))

(deftest test-main-1
  "Check the function zg.core/main."
  (testing "the function zg.core/main."
           ; use mock instead of jetty/run-jetty
           (with-redefs [jetty/run-jetty (fn [app port] port)]
             (is (= {:port 3000} (-main))))))

(deftest test-main-2
  "Check the function zg.core/main."
  (testing "the function zg.core/main."
           ; use mock instead of jetty/run-jetty
           (with-redefs [jetty/run-jetty (fn [app port] port)]
             (are [x y] (= x y)
                  {:port 2000} (-main "-p"     "2000")
                  {:port 2000} (-main "--port" "2000")))))

