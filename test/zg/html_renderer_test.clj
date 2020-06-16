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

(ns zg.html-renderer-test
  (:require [clojure.test :refer :all]
            [zg.html-renderer :refer :all]))

(require '[hiccup.page :as page])

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


(deftest test-mode->str-existence
    "Check that the zg.html-renderer/mode->str definition exists."
    (testing "if the zg.html-renderer/mode->str definition exists."
        (is (callable? 'zg.html-renderer/mode->str))))


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


(deftest test-render-error-page-existence
    "Check that the zg.html-renderer/render-error-page definition exists."
    (testing "if the zg.html-renderer/render-error-page definition exists."
        (is (callable? 'zg.html-renderer/render-error-page))))


(deftest test-render-front-page-existence
    "Check that the zg.html-renderer/render-front-page definition exists."
    (testing "if the zg.html-renderer/render-front-page definition exists."
        (is (callable? 'zg.html-renderer/render-front-page))))


(deftest test-render-users-existence
    "Check that the zg.html-renderer/render-users definition exists."
    (testing "if the zg.html-renderer/render-users definition exists."
        (is (callable? 'zg.html-renderer/render-users))))


(deftest test-render-user-info-existence
    "Check that the zg.html-renderer/render-user-info definition exists."
    (testing "if the zg.html-renderer/render-user-info definition exists."
        (is (callable? 'zg.html-renderer/render-user-info))))

;
; Test for function behaviours
;

(deftest test-render-html-header
    "Checking the function zg.html-renderer/render-html-header."
    (testing "the function zg.html-renderer/render-html-header."
        (are [x y] (= (slurp x) y)
            "test/expected/html_header1.html" (page/xhtml (render-html-header "" "" ""))
            "test/expected/html_header2.html" (page/xhtml (render-html-header "" "" "title"))
            "test/expected/html_header3.html" (page/xhtml (render-html-header "" "http://10.20.30.40/" ""))
            "test/expected/html_header4.html" (page/xhtml (render-html-header "" "http://10.20.30.40/" "title"))
            "test/expected/html_header5.html" (page/xhtml (render-html-header "word" "" ""))
            "test/expected/html_header6.html" (page/xhtml (render-html-header "word" "" "title"))
            "test/expected/html_header7.html" (page/xhtml (render-html-header "word" "http://10.20.30.40/" ""))
            "test/expected/html_header8.html" (page/xhtml (render-html-header "word" "http://10.20.30.40/" "title")))))

(deftest test-render-html-footer
    "Checking the function zg.html-renderer/render-html-footer."
    (testing "the function zg.html-renderer/render-html-footer."
        (is (= (slurp "test/expected/html_footer1.html") (page/xhtml (render-html-footer))))))

(deftest test-mode->str
    "Checking the function zg.html-renderer/mode->str."
    (testing "the function zg.html-renderer/mode->str."
        (are [x y] (= x (mode->str y))
            "whitelist"    :whitelist
            "blacklist"    :blacklist
            "atomic-typos" :atomic-typos
            "glossary"     :glossary
            "whitelist"    :other
            "whitelist"    nil)))

(deftest test-render-search-href
    "Checking the function zg.html-renderer/render-search-href."
    (testing "the function zg.html-renderer/render-search-href."
        (are [x y] (= (slurp x) y)
            "test/expected/search-href1.html"  (search-href "" :whitelist)
            "test/expected/search-href2.html"  (search-href "" :blacklist)
            "test/expected/search-href3.html"  (search-href "/" :whitelist)
            "test/expected/search-href4.html"  (search-href "/" :blacklist)
            "test/expected/search-href5.html"  (search-href "http://10.20.30.40/" :whitelist)
            "test/expected/search-href6.html"  (search-href "http://10.20.30.40/" :blacklist)
            "test/expected/search-href7.html"  (search-href "http://10.20.30.40/zg/" :whitelist)
            "test/expected/search-href8.html"  (search-href "http://10.20.30.40/zg/" :blacklist)
            "test/expected/search-href9.html"  (search-href ""  :atomic-typos)
            "test/expected/search-href10.html" (search-href "/" :atomic-typos)
            "test/expected/search-href11.html" (search-href "http://10.20.30.40/" :atomic-typos)
            "test/expected/search-href12.html" (search-href "http://10.20.30.40/zg/" :atomic-typos))))

(deftest test-render-search-field
    "Checking the function zg.html-renderer/render-search-field."
    (testing "the function zg.html-renderer/render-search-field."
        (are [x y] (= (slurp x) y)
            "test/expected/search-field1.html" (page/xhtml (render-search-field "word" "" :whitelist))
            "test/expected/search-field2.html" (page/xhtml (render-search-field "word" "" :blacklist))
            "test/expected/search-field3.html" (page/xhtml (render-search-field "word" "/" :whitelist))
            "test/expected/search-field4.html" (page/xhtml (render-search-field "word" "/" :blacklist))
            "test/expected/search-field5.html" (page/xhtml (render-search-field "word" "http://10.20.30.40/" :whitelist))
            "test/expected/search-field6.html" (page/xhtml (render-search-field "word" "http://10.20.30.40/" :blacklist))
            "test/expected/search-field7.html" (page/xhtml (render-search-field "word" "http://10.20.30.40/zg/" :whitelist))
            "test/expected/search-field8.html" (page/xhtml (render-search-field "word" "http://10.20.30.40/zg/" :blacklist)))))

(deftest test-render-name-field
    "Checking the function zg.html-renderer/render-name-field."
    (testing "the function zg.html-renderer/render-name-field."
        (are [x y] (= (slurp x) y)
            "test/expected/name-field1.html" (page/xhtml (render-name-field "user name" ""))
            "test/expected/name-field3.html" (page/xhtml (render-name-field "user name" "/"))
            "test/expected/name-field5.html" (page/xhtml (render-name-field "user name" "http://10.20.30.40/"))
            "test/expected/name-field7.html" (page/xhtml (render-name-field "user name" "http://10.20.30.40/zg/")))))

(deftest test-tab-class
    "Checking the function zg.html-renderer/tab-class."
    (testing "the function zg.html-renderer/tab-class."
        (are [x y] (= x y)
            {:class "active"} (tab-class 1 1)
            {:class "active"} (tab-class true true)
            {:class "active"} (tab-class "word" "word")
            nil               (tab-class 1 2)
            nil               (tab-class true false)
            nil               (tab-class "word" "world"))))

(deftest test-user-href
    "Checking the function zg.html-renderer/user-href."
    (testing "the function zg.html-renderer/user-href."
        (are [x y] (= x y)
            [:a {:href "user-whitelist?name=username"} "username"] (user-href "username" :whitelist)
            [:a {:href "user-blacklist?name=username"} "username"] (user-href "username" :blacklist)
            [:a {:href "user-whitelist?name=other-user"} "other-user"] (user-href "other-user" :whitelist)
            [:a {:href "user-blacklist?name=other-user"} "other-user"] (user-href "other-user" :blacklist))))

(deftest test-users-href
    "Checking the function zg.html-renderer/users-href."
    (testing "the function zg.html-renderer/users-href."
        (are [x y] (= x y)
            "url-prefix/users-whitelist"    (users-href "url-prefix/" :whitelist)
            "url-prefix/users-blacklist"    (users-href "url-prefix/" :blacklist)
            "url-prefix/users-atomic-typos" (users-href "url-prefix/" :atomic-typos)
            "url-prefix/users-glossary"     (users-href "url-prefix/" :glossary)
            nil                             (users-href "url-prefix/" :something-else))))

(deftest test-remember-me-href
    "Checking the function zg.html-renderer/remember-me-href."
    (testing "the function zg.html-renderer/remember-me-href."
        (are [x y] (= x y)
            "url-prefix/whitelist"    (remember-me-href "url-prefix/" :whitelist)
            "url-prefix/blacklist"    (remember-me-href "url-prefix/" :blacklist)
            "url-prefix/atomic-typos" (remember-me-href "url-prefix/" :atomic-typos)
            "url-prefix/glossary"     (remember-me-href "url-prefix/" :glossary)
            nil                       (remember-me-href "url-prefix/" :something-else))))

