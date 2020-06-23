;
;  (C) Copyright 2016, 2017, 2020  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns zg.server
  "Server module with functions to accept requests and send response back to users via HTTP.")

(require '[ring.util.response      :as http-response])
(require '[clojure.data.json       :as json])
(require '[clojure.xml             :as xml])
(require '[clojure.data.csv        :as csv])
(require '[clojure.tools.logging   :as log])

(require '[zg.db-interface         :as db-interface])
(require '[zg.html-renderer        :as html-renderer])
(require '[zg.dictionary-interface :as dictionary-interface])

(defn get-user-name
  "Retrieve user name from request."
  [request]
  (let [params        (:params request)
        cookies       (:cookies request)
        new-user-name (get params "user-name")
        old-user-name (get (get cookies "user-name") :value)
        user-name     (or new-user-name old-user-name)]
    (log/info "old user name" old-user-name)
    (log/info "new user name" new-user-name)
    (log/info "effective -> " user-name)
    user-name))

(defn get-title
  "Get page title from configuration that is part of request."
  [request]
  (-> (:configuration request)
      :display
      :app-name))

(defn get-emender-page
  "Retrieve link to Emender page from configuration.."
  [request]
  (-> (:configuration request)
      :display
      :emender-page))

(defn get-url-prefix
  "Retrieve URL prefix from configuration."
  [request]
  (-> (:configuration request)
      :server
      :url-prefix))

(defn finish-processing
  "Finish processing of HTTP request."
  [request search-results sources classes message title emender-page mode]
  (let [params        (:params request)
        cookies       (:cookies request)
        word          (get params "word")
        user-name     (get-user-name request)
        url-prefix    (get-url-prefix request)]
    (log/info "Incoming cookies: " cookies)
    (log/info "Word to search: " word)
    (log/trace "Search results: " search-results)
    (if user-name
      (-> (http-response/response
            (html-renderer/render-front-page word
                                             user-name
                                             search-results
                                             sources
                                             classes
                                             message
                                             url-prefix
                                             title
                                             emender-page
                                             mode))
          (http-response/set-cookie :user-name user-name {:max-age 36000000})
          (http-response/content-type "text/html"))
      (-> (http-response/response
            (html-renderer/render-front-page word
                                             user-name
                                             search-results
                                             sources
                                             classes
                                             message
                                             url-prefix
                                             title
                                             emender-page
                                             mode))
          (http-response/content-type "text/html")))))

(defn process-front-page
  "Function that prepares data for the front page."
  [request title emender-page]
  (let [params (:params request)]
    (finish-processing request nil nil nil nil title emender-page :whitelist)))

(defn process-atomic-typos
  "Function that prepares data for the Atomic typos front page."
  [request title emender-page]
  (let [params         (:params request)
        word           (get params "word")
        search-results (if (not (empty? word))
                         (db-interface/read-words-for-pattern word
                                                              :atomic-typos))]
    (finish-processing request
                       search-results
                       nil
                       nil
                       nil
                       title
                       emender-page
                       :atomic-typos)))

(defn sources->map
  "Provide mapping between source ID and source name."
  [sources]
  (into '{}
        (for [source sources] [(:id source) (:source source)])))

(defn classes->map
  "Provide mapping between class ID and class name."
  [classes]
  (into '{}
    (for [class classes] [(:id class) (:class class)])))

(defn process-glossary
  "Function that prepares data for the Glossary front page."
  [request title emender-page]
  (let [params         (:params request)
        word           (get params "word")
        search-results (if (not (empty? word))
                         (db-interface/read-words-for-pattern word :glossary))
        classes        (db-interface/read-word-classes)
        classes-map    (classes->map classes)
        sources        (db-interface/read-sources)
        sources-map    (sources->map sources)]
    (finish-processing request
                       search-results
                       sources-map
                       classes-map
                       nil
                       title
                       emender-page
                       :glossary)))

(defn add-word-message
  "Message that new word has been added into the dictionary."
  [word proper-word]
  (if (seq word)
    (if proper-word
      (str "The following word has been added into the dictionary: '" word "'. Thank you!")
      (str "Word with improper characters can not be added to the dictionary, sorry"))
      "No word has been added..."))

(defn add-words-message
  "Message that new words has been added into the dictionary."
  [words]
  (if (seq words)
    (if (> (count words) 1)
      (str "The following " (count words) " words have been added into the dictionary: '" (clojure.string/join "," words) "'. Thank you!")
      (str "The following word has been added into the dictionary: '" (clojure.string/join "," words) "'. Thank you!"))
    "No words have been added..."))

(defn split-words
  "Split input words."
  [input]
  (if input
    (->> (clojure.string/split input #"[\s,\n]")
         (filter seq))))

(defn read-string-parameter
  "Read string parameter from HTTP request."
  [request parameter-name]
  (if-let [parameter-value (-> (:params request)
                               (get parameter-name))]
    (clojure.string/trim parameter-value)))

(defn read-boolean-parameter
  "Read boolean parameter from HTTP request."
  [request parameter-name]
  (if-let [parameter-value (-> (:params request)
                               (get parameter-name))]
    (= "true" (clojure.string/trim parameter-value))
    false))

(defn read-integer-parameter
  "Read integer parameter from HTTP request."
  [request parameter-name]
  (if-let [parameter-value (-> (:params request)
                               (get parameter-name))]
    (Integer/parseInt (clojure.string/trim parameter-value))))

(defn process-add-word
  "Add word provided by user (together with its description) to the blacklist or into atomic typos."
  [request title emender-page mode]
  (clojure.pprint/pprint (:params request))
  (if (= mode :glossary)
    (let [word            (read-string-parameter request "new-word")
          description     (read-string-parameter request "description")
          correct-forms   (read-string-parameter request "correct-forms")
          incorrect-forms (read-string-parameter request "incorrect-forms")
          internal        (read-boolean-parameter request "internal")
          copyright       (read-boolean-parameter request "copyright")
          source          (read-integer-parameter request "source")
          class           (read-string-parameter request "class")
          use-it          (read-string-parameter request "use_it")
          user-name       (get-user-name request)
          message         (add-word-message word true)
          classes         (db-interface/read-word-classes)
          classes-map     (classes->map classes)
          sources         (db-interface/read-sources)
          sources-map     (sources->map sources)]
      (if (seq word)
        (dictionary-interface/store-word word
                                         description
                                         class
                                         use-it
                                         internal
                                         copyright
                                         source
                                         correct-forms
                                         incorrect-forms
                                         user-name
                                         :glossary))
      (finish-processing request
                         nil
                         sources-map
                         classes-map
                         message
                         title
                         emender-page
                         :glossary))
    (let [word         (read-string-parameter request "new-word")
          description  (read-string-parameter request "description")
          correct-form (read-string-parameter request "correct-word")
          word-class   (read-string-parameter request "class")
          internal     (read-string-parameter request "internal")
          copyright    (read-string-parameter request "copyright")
          source       (read-string-parameter request "source")
          proper-word  (dictionary-interface/proper-word-for-blacklist? word)
          user-name    (get-user-name request)
          message      (add-word-message word proper-word)
          sources      (db-interface/read-sources)]
      (if (and proper-word (seq word))
        (dictionary-interface/store-word word
                                         correct-form
                                         description
                                         user-name
                                         mode))
      (finish-processing request
                         nil
                         sources
                         nil
                         message
                         title
                         emender-page
                         mode))))

(defn process-add-words
  "Add words provided by user (together with its description) to the blacklist or into atomic typos."
  [request title emender-page mode]
  (let [input        (-> (:params request) (get "new-words"))
        words        (split-words input)
        proper-words (filter dictionary-interface/proper-word-for-whitelist? words)
        user-name    (get-user-name request)
        message      (add-words-message proper-words)]
    (if (seq proper-words)
      (dictionary-interface/store-words proper-words user-name mode))
    (finish-processing request nil nil nil message title emender-page mode)))

(defn perform-operation
  "Perform the requested operation - delete or undelete."
  [request mode]
  (let [to-delete   (-> (:params request)
                        (get "delete"))
        to-undelete (-> (:params request)
                        (get "undelete"))]
    (log/info "to delete" to-delete)
    (log/info "to undelete" to-undelete)
    (if to-delete
      (db-interface/delete-word to-delete mode))
    (if to-undelete
      (db-interface/undelete-word to-undelete mode))))

(defn process-whitelist
  "Function that prepares data for the whitelist front page."
  [request title emender-page]
  ; perform the operation selected by user on the web UI (delete, undelete etc.)
  (perform-operation request :whitelist)
  (let [params         (:params request)
        word           (get params "word")
        search-results (if (not (empty? word))
                         (db-interface/read-words-for-pattern word :whitelist))]
    (finish-processing request
                       search-results
                       nil
                       nil
                       nil
                       title
                       emender-page
                       :whitelist)))

(defn process-blacklist
  "Function that prepares data for the blacklist front page."
  [request title emender-page]
  ; perform the operation selected by user on the web UI (delete, undelete etc.)
  (perform-operation request :blacklist)
  (let [params         (:params request)
        word           (get params "word")
        search-results (if (not (empty? word))
                         (db-interface/read-words-for-pattern word :blacklist))]
    (finish-processing request
                       search-results
                       nil
                       nil
                       nil
                       title
                       emender-page
                       :blacklist)))

(defn process-all-words
  "Read all words from the selected dictionary and display them to user on generated page."
  [request title emender-page mode]
  (perform-operation request mode)
  (let [search-results (db-interface/read-all-words mode)
        classes        (db-interface/read-word-classes)
        classes-map    (classes->map classes)
        sources        (db-interface/read-sources)
        sources-map    (sources->map sources)]
    (finish-processing request
                       search-results
                       sources-map
                       classes-map
                       nil
                       title
                       emender-page
                       mode)))

(defn process-deleted-words
  "Read all deleted words from the selected dictionary and display them to user on generated page."
  [request title emender-page mode]
  (perform-operation request mode)
  (let [search-results (db-interface/read-deleted-words mode)
        classes        (db-interface/read-word-classes)
        classes-map    (classes->map classes)
        sources        (db-interface/read-sources)
        sources-map    (sources->map sources)]
    (finish-processing request
                       search-results
                       sources-map
                       classes-map
                       nil
                       title
                       emender-page
                       mode)))

(defn process-active-words
  "Read all nondeleted words from the selected dictionary and display them to user on generated page."
  [request title emender-page mode]
  (perform-operation request mode)
  (let [search-results (db-interface/read-active-words mode)
        classes        (db-interface/read-word-classes)
        classes-map    (classes->map classes)
        sources        (db-interface/read-sources)
        sources-map    (sources->map sources)]
    (finish-processing request
                       search-results
                       sources-map
                       classes-map
                       nil
                       title
                       emender-page
                       mode)))

(defn read-changes-statistic
  "Read statistic about changes from database."
  []
  (db-interface/read-changes-statistic))

(defn read-changes
  "Read all changes from database."
  []
  (db-interface/read-changes))

(defn read-changes-for-user
  "Read changes made by one user from database."
  [user-name]
  (db-interface/read-changes-for-user user-name))

(defn process-user-list
  "Prepare page with list of users."
  [request title mode]
  (let [changes-statistic (read-changes-statistic)
        changes           (read-changes)
        user-name         (get-user-name request)
        url-prefix        (get-url-prefix request)]
    (log/info "stat"    changes-statistic)
    (log/info "changes" changes)
    (if user-name
      (-> (http-response/response (html-renderer/render-users user-name
                                                              changes-statistic
                                                              changes
                                                              url-prefix
                                                              title
                                                              mode))
          (http-response/set-cookie :user-name user-name {:max-age 36000000})
          (http-response/content-type "text/html"))
      (-> (http-response/response (html-renderer/render-users user-name
                                                              changes-statistic
                                                              changes
                                                              url-prefix
                                                              title
                                                              mode))
          (http-response/content-type "text/html")))))

(defn process-user-info
  "Prepare page with info about one selected user."
  [request title mode]
  (let [params        (:params request)
        selected-user (get params "name")
        changes       (read-changes-for-user selected-user)
        user-name     (get-user-name request)
        url-prefix    (get-url-prefix request)]
    (if user-name
      (-> (http-response/response (html-renderer/render-user-info selected-user
                                                                  changes
                                                                  url-prefix
                                                                  title
                                                                  mode))
          (http-response/set-cookie :user-name user-name {:max-age 36000000})
          (http-response/content-type "text/html"))
      (-> (http-response/response (html-renderer/render-user-info selected-user
                                                                  changes
                                                                  url-prefix
                                                                  title
                                                                  mode))
          (http-response/content-type "text/html")))))


(defn words->json
  "Convert all words into JSON format."
  [words]
  (json/write-str words))

(defn words->text
  "Convert all words into text."
  [words]
  (->>
    (for [word words
          :when (= (:deleted word) 0)]
      (clojure.string/join
        "\t"
        [(:word word) (:description word) (:user word) (:datetime word)
         (:class_name word) (:source_name word) (:product word)
         (:use word) (:correct_forms word) (:incorrect_forms word)
         (:see_also word) (:internal word) (:verified word) (:copyrighted word)]))
    (clojure.string/join "\n")))

(defn words->xml
  "Convert all words into XML format."
  [words]
  (with-out-str
    (xml/emit {:tag :whitelist,
               :content (for [word words]
                          {:tag :word,
                           :attrs {:added-by        (:user word),
                                   :date-time       (:datetime word),
                                   :deleted         (:deleted word),
                                   :description     (:description word),
                                   :class           (:class_name word),
                                   :source          (:source word),
                                   :product         (:product word),
                                   :use             (:use word),
                                   :correct_forms   (:correct_forms word),
                                   :incorrect_forms (:incorrect_forms word),
                                   :see_also        (:see_also word),
                                   :internal        (:internal word),
                                   :verified        (:verified word),
                                   :copyrighted     (:copyrighted word)},
                           :content [(:word word)]})})))

(defn words->edn
  "Convert all words into EDN format."
  [words]
  (with-out-str
    (clojure.pprint/pprint words)))

(defn words->csv
  "Convert all words into CSV format."
  [words]
  (with-out-str
    (csv/write-csv *out* words)))

(defn words->vector
  "Convert all words into vector."
  [words]
  (for [word words]
    (vals word)))

(defn process-wordlist-json
  "Prepare report with all words in JSON format."
  [request dictionary-type]
  (let [search-results (db-interface/read-all-words dictionary-type)
        json-output    (words->json search-results)]
    (-> (http-response/response json-output)
        (http-response/content-type "application/json"))))

(defn process-wordlist-text
  "Prepare report with all words in plain text format."
  [request dictionary-type]
  (let [search-results (db-interface/read-all-words dictionary-type)
        text-output    (words->text search-results)]
    (-> (http-response/response text-output)
        (http-response/content-type "text/plain"))))

(defn process-wordlist-xml
  "Prepare report with all words in XML format."
  [request dictionary-type]
  (let [search-results (db-interface/read-all-words dictionary-type)
        xml-output     (words->xml search-results)]
    (-> (http-response/response xml-output)
        (http-response/content-type "text/xml"))))

(defn process-wordlist-edn
  "Prepare report with all words in EDN format."
  [request dictionary-type]
  (let [search-results (db-interface/read-all-words dictionary-type)
        edn-output     (words->edn search-results)]
    (-> (http-response/response edn-output)
        (http-response/content-type "application/edn"))))

(defn process-wordlist-csv
  "Prepare report with all words in CSV format."
  [request dictionary-type]
  (let [search-results (db-interface/read-all-words dictionary-type)
        words          (words->vector search-results)
        csv-output     (words->csv words)]
    (-> (http-response/response csv-output)
        (http-response/content-type "text/csv; charset=utf-8"))))

(defn process-different-spelling-words
  "Prepare report with different spelled words in JSON format."
  [request]
  (let [search-results (db-interface/read-different-spelling-words)
        json-output    (words->json search-results)]
    (-> (http-response/response json-output)
        (http-response/content-type "application/json"))))

;defn process-delete-word
;   [request]
;   )
;

;defn process-undelete-word
;   [request]
;   )

;(defn process-find-words
;    [request]
;    ; not needed ATM

(defn return-file
  "Creates HTTP response containing content of specified file.
     Special value nil / HTTP response 404 is returned in case of any I/O error."
  [^String file-name content-type]
  (let [file (new java.io.File "www" file-name)]
    (log/info "Returning file " (.getAbsolutePath file))
    (if (.exists file)
      (-> (http-response/response file)
          (http-response/content-type content-type))
      (log/error "return-file(): can not access file: " (.getName file)))))

(defn handler
  "Handler that is called by Ring for all requests received from user(s)."
  [request]
  (log/info "request URI: " (request :uri))
  (let [uri          (request :uri)
        title        (get-title request)
        emender-page (get-emender-page request)]
    (condp = uri
      "/favicon.ico"                (return-file "favicon.ico" "image/x-icon")
      "/bootstrap.min.css"          (return-file "bootstrap.min.css" "text/css")
      "/smearch.css"                (return-file "smearch.css" "text/css")
      "/bootstrap.min.js"           (return-file "bootstrap.min.js" "application/javascript")
      "/"                           (process-front-page    request title emender-page)
      "/whitelist"                  (process-whitelist     request title emender-page)
      "/blacklist"                  (process-blacklist     request title emender-page)
      "/atomic-typos"               (process-atomic-typos  request title emender-page)
      "/glossary"                   (process-glossary      request title emender-page)
      "/all-words-in-whitelist"     (process-all-words     request title emender-page :whitelist)
      "/all-words-in-blacklist"     (process-all-words     request title emender-page :blacklist)
      "/all-words-in-atomic-typos"  (process-all-words     request title emender-page :atomic-typos)
      "/all-words-in-glossary"      (process-all-words     request title emender-page :glossary)
      "/active-words-in-whitelist"  (process-active-words  request title emender-page :whitelist)
      "/active-words-in-blacklist"  (process-active-words  request title emender-page :blacklist)
      "/active-words-in-atomic-typos"  (process-active-words     request title emender-page :atomic-typos)
      "/active-words-in-glossary"      (process-active-words     request title emender-page :glossary)
      "/deleted-words-in-whitelist"    (process-deleted-words request title emender-page :whitelist)
      "/deleted-words-in-blacklist"    (process-deleted-words request title emender-page :blacklist)
      "/deleted-words-in-atomic-typos" (process-deleted-words     request title emender-page :atomic-typos)
      "/deleted-words-in-glossary"     (process-deleted-words     request title emender-page :glossary)
      "/add-word-to-blacklist"    (process-add-word      request title emender-page :blacklist)
      ;"/find-words"       (process-find-words    request)
      "/add-words-to-whitelist"   (process-add-words   request title emender-page :whitelist)
      "/add-word-to-atomic-typos" (process-add-word    request title emender-page :atomic-typos)
      "/add-word-to-glossary"     (process-add-word    request title emender-page :glossary)
      "/users-whitelist"        (process-user-list     request title :whitelist)
      "/users-blacklist"        (process-user-list     request title :blacklist)
      "/users-atomic-typos"     (process-user-list     request title :atomic-typos)
      "/user-whitelist"         (process-user-info     request title :whitelist)
      "/user-blacklist"         (process-user-info     request title :blacklist)
      "/user-glossary"          (process-user-info     request title :glossary)
      "/all-words/json"         (process-wordlist-json request nil)
      "/all-words/text"         (process-wordlist-text request nil)
      "/all-words/xml"          (process-wordlist-xml  request nil)
      "/all-words/edn"          (process-wordlist-edn  request nil)
      "/all-words/csv"          (process-wordlist-csv  request nil)
      "/whitelist/json"         (process-wordlist-json request :whitelist)
      "/whitelist/text"         (process-wordlist-text request :whitelist)
      "/whitelist/xml"          (process-wordlist-xml  request :whitelist)
      "/whitelist/edn"          (process-wordlist-edn  request :whitelist)
      "/whitelist/csv"          (process-wordlist-csv  request :whitelist)
      "/blacklist/json"         (process-wordlist-json request :blacklist)
      "/blacklist/text"         (process-wordlist-text request :blacklist)
      "/blacklist/xml"          (process-wordlist-xml  request :blacklist)
      "/blacklist/edn"          (process-wordlist-edn  request :blacklist)
      "/blacklist/csv"          (process-wordlist-csv  request :blacklist)
      "/glossary/json"          (process-wordlist-json request :glossary)
      "/glossary/text"          (process-wordlist-text request :glossary)
      "/glossary/xml"           (process-wordlist-xml  request :glossary)
      "/glossary/edn"           (process-wordlist-edn  request :glossary)
      "/glossary/csv"           (process-wordlist-csv  request :glossary)
      "/different-spelling-words/json" (process-different-spelling-words request)
      ;"/delete-word"           (process-delete-word   request)
      ;"/undelete-word"         (process-undelete-word request)
    )))

