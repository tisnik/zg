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

(deftest test-get-user-name-not-NPE
    "Check the function get-user-name."
    (testing "the function get-user-name."
        (are [x y] (= x (get-user-name y))
            nil nil)))

(deftest test-get-user-name
    "Check the function get-user-name."
    (testing "the function get-user-name."
        (are [x y] (= x (get-user-name y))
            "new user" {:params  {"user-name" "new user"}}
            "new user" {:params  {"user-name" "new user"}
                        :cookies {"user-name" {:value "old user"}}}
            "old user" {:cookies {"user-name" {:value "old user"}}})))

(deftest test-get-title
    "Check the function get-title."
    (testing "the function get-title."
        (are [x y] (= x (get-title y))
            "ZG" {:configuration {:display {:app-name "ZG"}}}
            nil  {:configuration {:display {:app-name nil}}}
            nil  {:configuration {:display nil}}
            nil  {:configuration {:something-else nil}}
            nil  {:configuration nil}
            nil  {:something-else nil}
            nil  nil)))

(deftest test-get-emender-page
    "Check the function get-emender-page."
    (testing "the function get-emender-page."
        (are [x y] (= x (get-emender-page y))
            "http://www.emender.org" {:configuration {:display {:emender-page "http://www.emender.org"}}}
            nil  {:configuration {:display {:emender-page nil}}}
            nil  {:configuration {:display nil}}
            nil  {:configuration {:something-else nil}}
            nil  {:configuration nil}
            nil  {:something-else nil}
            nil  nil)))

(deftest test-get-url-prefix
    "Check the function get-url-prefix."
    (testing "the function get-url-prefix."
        (are [x y] (= x (get-url-prefix y))
            "http://localhost" {:configuration {:server {:url-prefix "http://localhost"}}}
            nil  {:configuration {:server {:url-prefix nil}}}
            nil  {:configuration {:server nil}}
            nil  {:configuration {:something-else nil}}
            nil  {:configuration nil}
            nil  {:something-else nil}
            nil  nil)))

