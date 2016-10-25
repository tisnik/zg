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

;
; Tests for various functions existence
;

(deftest test-get-user-name-existence
    "Check that the zg.server/get-user-name definition exists."
    (testing "if the zg.server/get-user-name definition exists."
        (is (callable? 'zg.server/get-user-name))))


(deftest test-get-title-existence
    "Check that the zg.server/get-title definition exists."
    (testing "if the zg.server/get-title definition exists."
        (is (callable? 'zg.server/get-title))))


(deftest test-get-url-prefix-existence
    "Check that the zg.server/get-url-prefix definition exists."
    (testing "if the zg.server/get-url-prefix definition exists."
        (is (callable? 'zg.server/get-url-prefix))))


(deftest test-finish-processing-existence
    "Check that the zg.server/finish-processing definition exists."
    (testing "if the zg.server/finish-processing definition exists."
        (is (callable? 'zg.server/finish-processing))))


(deftest test-process-front-page-existence
    "Check that the zg.server/process-front-page definition exists."
    (testing "if the zg.server/process-front-page definition exists."
        (is (callable? 'zg.server/process-front-page))))


(deftest test-process-whitelist-existence
    "Check that the zg.server/process-whitelist definition exists."
    (testing "if the zg.server/process-whitelist definition exists."
        (is (callable? 'zg.server/process-whitelist))))


(deftest test-process-blacklist-existence
    "Check that the zg.server/process-blacklist definition exists."
    (testing "if the zg.server/process-blacklist definition exists."
        (is (callable? 'zg.server/process-blacklist))))


(deftest test-store-word-existence
    "Check that the zg.server/store-word definition exists."
    (testing "if the zg.server/store-word definition exists."
        (is (callable? 'zg.server/store-word))))


(deftest test-store-words-existence
    "Check that the zg.server/store-words definition exists."
    (testing "if the zg.server/store-words definition exists."
        (is (callable? 'zg.server/store-words))))


(deftest test-add-word-message-existence
    "Check that the zg.server/add-word-message definition exists."
    (testing "if the zg.server/add-word-message definition exists."
        (is (callable? 'zg.server/add-word-message))))


(deftest test-add-words-message-existence
    "Check that the zg.server/add-words-message definition exists."
    (testing "if the zg.server/add-words-message definition exists."
        (is (callable? 'zg.server/add-words-message))))


(deftest test-split-words-existence
    "Check that the zg.server/split-words definition exists."
    (testing "if the zg.server/split-words definition exists."
        (is (callable? 'zg.server/split-words))))


(deftest test-proper-word-for-whitelist?-existence
    "Check that the zg.server/proper-word-for-whitelist? definition exists."
    (testing "if the zg.server/proper-word-for-whitelist? definition exists."
        (is (callable? 'zg.server/proper-word-for-whitelist?))))


(deftest test-proper-word-for-blacklist?-existence
    "Check that the zg.server/proper-word-for-blacklist? definition exists."
    (testing "if the zg.server/proper-word-for-blacklist? definition exists."
        (is (callable? 'zg.server/proper-word-for-blacklist?))))


(deftest test-process-add-word-existence
    "Check that the zg.server/process-add-word definition exists."
    (testing "if the zg.server/process-add-word definition exists."
        (is (callable? 'zg.server/process-add-word))))


(deftest test-process-add-words-existence
    "Check that the zg.server/process-add-words definition exists."
    (testing "if the zg.server/process-add-words definition exists."
        (is (callable? 'zg.server/process-add-words))))


(deftest test-perform-operation-existence
    "Check that the zg.server/perform-operation definition exists."
    (testing "if the zg.server/perform-operation definition exists."
        (is (callable? 'zg.server/perform-operation))))


(deftest test-process-all-words-existence
    "Check that the zg.server/process-all-words definition exists."
    (testing "if the zg.server/process-all-words definition exists."
        (is (callable? 'zg.server/process-all-words))))


(deftest test-process-deleted-words-existence
    "Check that the zg.server/process-deleted-words definition exists."
    (testing "if the zg.server/process-deleted-words definition exists."
        (is (callable? 'zg.server/process-deleted-words))))


(deftest test-process-active-words-existence
    "Check that the zg.server/process-active-words definition exists."
    (testing "if the zg.server/process-active-words definition exists."
        (is (callable? 'zg.server/process-active-words))))


(deftest test-read-changes-statistic-existence
    "Check that the zg.server/read-changes-statistic definition exists."
    (testing "if the zg.server/read-changes-statistic definition exists."
        (is (callable? 'zg.server/read-changes-statistic))))


(deftest test-read-changes-existence
    "Check that the zg.server/read-changes definition exists."
    (testing "if the zg.server/read-changes definition exists."
        (is (callable? 'zg.server/read-changes))))


(deftest test-read-changes-for-user-existence
    "Check that the zg.server/read-changes-for-user definition exists."
    (testing "if the zg.server/read-changes-for-user definition exists."
        (is (callable? 'zg.server/read-changes-for-user))))


(deftest test-process-user-list-existence
    "Check that the zg.server/process-user-list definition exists."
    (testing "if the zg.server/process-user-list definition exists."
        (is (callable? 'zg.server/process-user-list))))


(deftest test-process-user-info-existence
    "Check that the zg.server/process-user-info definition exists."
    (testing "if the zg.server/process-user-info definition exists."
        (is (callable? 'zg.server/process-user-info))))


(deftest test-words->json-existence
    "Check that the zg.server/words->json definition exists."
    (testing "if the zg.server/words->json definition exists."
        (is (callable? 'zg.server/words->json))))


(deftest test-words->text-existence
    "Check that the zg.server/words->text definition exists."
    (testing "if the zg.server/words->text definition exists."
        (is (callable? 'zg.server/words->text))))


(deftest test-words->xml-existence
    "Check that the zg.server/words->xml definition exists."
    (testing "if the zg.server/words->xml definition exists."
        (is (callable? 'zg.server/words->xml))))


(deftest test-words->edn-existence
    "Check that the zg.server/words->edn definition exists."
    (testing "if the zg.server/words->edn definition exists."
        (is (callable? 'zg.server/words->edn))))


(deftest test-process-wordlist-json-existence
    "Check that the zg.server/process-wordlist-json definition exists."
    (testing "if the zg.server/process-wordlist-json definition exists."
        (is (callable? 'zg.server/process-wordlist-json))))


(deftest test-process-wordlist-text-existence
    "Check that the zg.server/process-wordlist-text definition exists."
    (testing "if the zg.server/process-wordlist-text definition exists."
        (is (callable? 'zg.server/process-wordlist-text))))


(deftest test-process-wordlist-xml-existence
    "Check that the zg.server/process-wordlist-xml definition exists."
    (testing "if the zg.server/process-wordlist-xml definition exists."
        (is (callable? 'zg.server/process-wordlist-xml))))


(deftest test-process-wordlist-edn-existence
    "Check that the zg.server/process-wordlist-edn definition exists."
    (testing "if the zg.server/process-wordlist-edn definition exists."
        (is (callable? 'zg.server/process-wordlist-edn))))


(deftest test-return-file-existence
    "Check that the zg.server/return-file definition exists."
    (testing "if the zg.server/return-file definition exists."
        (is (callable? 'zg.server/return-file))))


(deftest test-handler-existence
    "Check that the zg.server/handler definition exists."
    (testing "if the zg.server/handler definition exists."
        (is (callable? 'zg.server/handler))))

;
; Test for function behaviours
;

