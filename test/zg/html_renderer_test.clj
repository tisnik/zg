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

(ns zg.html-renderer-test
  (:require [clojure.test :refer :all]
            [zg.html-renderer :refer :all]))

;
; Common functions used by tests.
;

(defn callable?
    "Test if given function-name is bound to the real function."
    [function-name]
    (clojure.test/function? function-name))

;
; Test for existence of several functions.
;

(deftest test-render-html-header-existence
    "Check that the zg.html-renderer/render-html-header definition exists."
    (testing "if the zg.html-renderer/render-html-header definition exists."
        (is (callable? 'zg.html-renderer/render-html-header))))


(deftest test-render-html-footer-existence
    "Check that the zg.html-renderer/render-html-footer definition exists."
    (testing "if the zg.html-renderer/render-html-footer definition exists."
        (is (callable? 'zg.html-renderer/render-html-footer))))


(deftest test-search-href-existence
    "Check that the zg.html-renderer/search-href definition exists."
    (testing "if the zg.html-renderer/search-href definition exists."
        (is (callable? 'zg.html-renderer/search-href))))


(deftest test-render-search-field-existence
    "Check that the zg.html-renderer/render-search-field definition exists."
    (testing "if the zg.html-renderer/render-search-field definition exists."
        (is (callable? 'zg.html-renderer/render-search-field))))


(deftest test-render-name-field-existence
    "Check that the zg.html-renderer/render-name-field definition exists."
    (testing "if the zg.html-renderer/render-name-field definition exists."
        (is (callable? 'zg.html-renderer/render-name-field))))


(deftest test-tab-class-existence
    "Check that the zg.html-renderer/tab-class definition exists."
    (testing "if the zg.html-renderer/tab-class definition exists."
        (is (callable? 'zg.html-renderer/tab-class))))


(deftest test-user-href-existence
    "Check that the zg.html-renderer/user-href definition exists."
    (testing "if the zg.html-renderer/user-href definition exists."
        (is (callable? 'zg.html-renderer/user-href))))


(deftest test-users-href-existence
    "Check that the zg.html-renderer/users-href definition exists."
    (testing "if the zg.html-renderer/users-href definition exists."
        (is (callable? 'zg.html-renderer/users-href))))


(deftest test-remember-me-href-existence
    "Check that the zg.html-renderer/remember-me-href definition exists."
    (testing "if the zg.html-renderer/remember-me-href definition exists."
        (is (callable? 'zg.html-renderer/remember-me-href))))


(deftest test-render-navigation-bar-section-existence
    "Check that the zg.html-renderer/render-navigation-bar-section definition exists."
    (testing "if the zg.html-renderer/render-navigation-bar-section definition exists."
        (is (callable? 'zg.html-renderer/render-navigation-bar-section))))


