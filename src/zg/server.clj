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

(ns zg.server
    "Server module with functions to accept requests and send response back to users via HTTP.")

(require '[ring.util.response     :as http-response])
(require '[clojure.data.json      :as json])
(require '[clojure.xml            :as xml])
(require '[clojure.data.csv       :as csv])

(require '[zg.db-interface        :as db-interface])
(require '[zg.html-renderer       :as html-renderer])

(defn println-and-flush
    "Original (println) has problem with syncing when it's called from more threads.
     This function is a bit better because it flushes all output immediatelly."
    [& more]
    (.write *out* (str (clojure.string/join " " more) "\n"))
    (flush))

(defn get-user-name
    [request]
    (let [params        (:params request)
          cookies       (:cookies request)
          new-user-name (get params "user-name")
          old-user-name (get (get cookies "user-name") :value)
          user-name     (or new-user-name old-user-name)]
        (println "old" old-user-name)
        (println "new" new-user-name)
        (println "-> " user-name)
        user-name))

(defn get-title
    [request]
    (-> (:configuration request)
        :display
        :app-name))

(defn get-emender-page
    [request]
    (-> (:configuration request)
        :display
        :emender-page))

(defn get-url-prefix
    [request]
    (-> (:configuration request)
        :server
        :url-prefix))

(defn finish-processing
    [request search-results message title emender-page mode]
    (let [params        (:params request)
          cookies       (:cookies request)
          word          (get params "word")
          user-name     (get-user-name request)
          url-prefix    (get-url-prefix request)]
        (println-and-flush "Incoming cookies: " cookies)
        (println word)
        (println search-results)
        (if user-name
            (-> (http-response/response (html-renderer/render-front-page word user-name search-results message url-prefix title emender-page mode))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-front-page word user-name search-results message url-prefix title emender-page mode))
                (http-response/content-type "text/html")))))

(defn process-front-page
    "Function that prepares data for the front page."
    [request title emender-page]
    (let [params         (:params request)]
        (finish-processing request nil nil title emender-page :whitelist)))

(defn process-whitelist
    "Function that prepares data for the whitelist front page."
    [request title emender-page]
    (let [params         (:params request)
          word           (get params "word")
          search-results (if (not (empty? word))
                             (db-interface/read-words-for-pattern word :whitelist))]
        (finish-processing request search-results nil title emender-page :whitelist)))

(defn process-blacklist
    "Function that prepares data for the blacklist front page."
    [request title emender-page]
    (let [params         (:params request)
          word           (get params "word")
          search-results (if (not (empty? word))
                             (db-interface/read-words-for-pattern word :blacklist))]
        (finish-processing request search-results nil title emender-page :blacklist)))

(defn store-word
    "Store one word into the dictionary."
    [word description user-name dictionary-type]
    (println "About to store following word:" word " with description: " description
             " into dictionary: " (str dictionary-type))
    (db-interface/add-new-word-into-dictionary word description (or user-name "(*unknown*)") dictionary-type))

(defn store-words
    "Store sequence of words into the dictionary."
    [words user-name dictionary-type]
    (println "About to store following words:" words)
    (doseq [word words]
        (db-interface/add-new-word-into-dictionary word user-name dictionary-type)))

(defn add-word-message
    [word proper-word]
    (if (seq word)
        (if proper-word
            (str "The following word has been added into the dictionary: '" word "'. Thank you!")
            (str "Word with improper characters can not be added to the dictionary, sorry"))
        "No word has been added..."))

(defn add-words-message
    [words]
    (if (seq words)
        (if (> (count words) 1)
            (str "The following " (count words) " words have been added into the dictionary: '" (clojure.string/join "," words) "'. Thank you!")
            (str "The following word has been added into the dictionary: '" (clojure.string/join "," words) "'. Thank you!"))
        "No words have been added..."))

(defn split-words
    [input]
    (if input
        (->> (clojure.string/split input #"[\s,\n]")
             (filter seq))))

(defn proper-word?
    [word]
    (re-matches #"[A-Za-z0-9.'\-\s_]+" word))

(defn process-add-word
    "Add word provided by user (together with its description) to the blacklist."
    [request title emender-page mode]
    (let [word        (-> (:params request) (get "new-word") clojure.string/trim)
          description (-> (:params request) (get "description") clojure.string/trim)
          proper-word (proper-word? word)
          user-name (get-user-name request)
          message   (add-word-message word proper-word)]
          (if (and proper-word (seq word))
              (store-word word description user-name mode))
        (finish-processing request nil message title emender-page mode)))

(defn process-add-words
    [request title emender-page mode]
    (let [input        (-> (:params request) (get "new-words"))
          words        (split-words input)
          proper-words (filter proper-word? words)
          user-name    (get-user-name request)
          message      (add-words-message proper-words)]
          (if (seq proper-words)
              (store-words proper-words user-name mode))
        (finish-processing request nil message title emender-page mode)))

(defn perform-operation
    [request mode]
    (let [to-delete   (-> (:params request) (get "delete"))
          to-undelete (-> (:params request) (get "undelete"))]
          (println "to delete" to-delete)
          (println "to undelete" to-undelete)
          (if to-delete
              (db-interface/delete-word to-delete mode))
          (if to-undelete
              (db-interface/undelete-word to-undelete mode))))

(defn process-all-words
    "Read all words from the selected dictionary and display them to user on generated page."
    [request title emender-page mode]
    (perform-operation request mode)
    (let [search-results (db-interface/read-all-words mode)]
        (finish-processing request search-results nil title emender-page mode)))

(defn process-deleted-words
    "Read all deleted words from the selected dictionary and display them to user on generated page."
    [request title emender-page mode]
    (perform-operation request mode)
    (let [search-results (db-interface/read-deleted-words mode)]
        (finish-processing request search-results nil title emender-page mode)))

(defn process-active-words
    "Read all nondeleted words from the selected dictionary and display them to user on generated page."
    [request title emender-page mode]
    (perform-operation request mode)
    (let [search-results (db-interface/read-active-words mode)]
        (finish-processing request search-results nil title emender-page mode)))

(defn read-changes-statistic
    []
    (db-interface/read-changes-statistic))

(defn read-changes
    []
    (db-interface/read-changes))

(defn read-changes-for-user
    [user-name]
    (db-interface/read-changes-for-user user-name))

(defn process-user-list
    [request title mode]
    (let [changes-statistic (read-changes-statistic)
          changes           (read-changes)
          user-name         (get-user-name request)
          url-prefix        (get-url-prefix request)]
        (println "stat"    changes-statistic)
        (println "changes" changes)
        (if user-name
            (-> (http-response/response (html-renderer/render-users user-name changes-statistic changes url-prefix title mode))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-users user-name changes-statistic changes url-prefix title mode))
                (http-response/content-type "text/html")))))

(defn process-user-info
    [request title mode]
    (let [params            (:params request)
          selected-user     (get params "name")
          changes           (read-changes-for-user selected-user)
          user-name         (get-user-name request)
          url-prefix        (get-url-prefix request)]
        (if user-name
            (-> (http-response/response (html-renderer/render-user-info selected-user changes url-prefix title mode))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-user-info selected-user changes url-prefix title mode))
                (http-response/content-type "text/html")))))

(defn words->json
    [words]
    (json/write-str words))

(defn words->text
    [words]
    (->> (for [word words :when (= (:deleted word) 0)]
             (str (:word word) "\t" (:description word)))
         (clojure.string/join "\n")))

(defn words->xml
    [words]
    (with-out-str
        (xml/emit {:tag :whitelist :content (for [word words]
                                             {:tag :word :attrs {
                                                             :added-by     (:user word)
                                                             :date-time    (:datetime word)
                                                             :deleted      (:deleted word)
                                                             :description  (:description word)}
                                                             :content      [(:word word)]})})))

(defn words->edn
    [words]
    (with-out-str
        (clojure.pprint/pprint words)))

(defn words->csv
    [words]
    (with-out-str
        (csv/write-csv *out* words)))

(defn words->vector
    [words]
    (for [word words]
        (vals word)))

(defn process-wordlist-json
    [request dictionary-type]
    (let [search-results (db-interface/read-all-words dictionary-type)
          json-output    (words->json search-results)]
        (-> (http-response/response json-output)
            (http-response/content-type "application/json"))))

(defn process-wordlist-text
    [request dictionary-type]
    (let [search-results (db-interface/read-all-words dictionary-type)
          text-output    (words->text search-results)]
        (-> (http-response/response text-output)
            (http-response/content-type "text/plain"))))

(defn process-wordlist-xml
    [request dictionary-type]
    (let [search-results (db-interface/read-all-words dictionary-type)
          xml-output     (words->xml search-results)]
        (-> (http-response/response xml-output)
            (http-response/content-type "text/xml"))))

(defn process-wordlist-edn
    [request dictionary-type]
    (let [search-results (db-interface/read-all-words dictionary-type)
          edn-output     (words->edn search-results)]
        (-> (http-response/response edn-output)
            (http-response/content-type "application/edn"))))

(defn process-wordlist-csv
    [request dictionary-type]
    (let [search-results (db-interface/read-all-words dictionary-type)
          words          (words->vector search-results)
          csv-output     (words->csv words)]
        (-> (http-response/response csv-output)
            (http-response/content-type "text/csv; charset=utf-8"))))

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
    [file-name content-type]
    (let [file (new java.io.File "www" file-name)]
        (println-and-flush "Returning file " (.getAbsolutePath file))
        (if (.exists file)
            (-> (http-response/response file)
                (http-response/content-type content-type))
            (println-and-flush "return-file(): can not access file: " (.getName file)))))

(defn handler
    "Handler that is called by Ring for all requests received from user(s)."
    [request]
    (println-and-flush "request URI: " (request :uri))
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
            "/all-words-in-whitelist"     (process-all-words     request title emender-page :whitelist)
            "/all-words-in-blacklist"     (process-all-words     request title emender-page :blacklist)
            "/active-words-in-whitelist"  (process-active-words  request title emender-page :whitelist)
            "/active-words-in-blacklist"  (process-active-words  request title emender-page :blacklist)
            "/deleted-words-in-whitelist" (process-deleted-words request title emender-page :whitelist)
            "/deleted-words-in-blacklist" (process-deleted-words request title emender-page :blacklist)
            "/add-word-to-blacklist"  (process-add-word      request title emender-page :blacklist)
            ;"/find-words"       (process-find-words    request)
            "/add-words-to-whitelist" (process-add-words     request title emender-page :whitelist)
            "/users-whitelist"        (process-user-list     request title :whitelist)
            "/users-blacklist"        (process-user-list     request title :blacklist)
            "/user-whitelist"         (process-user-info     request title :whitelist)
            "/user-blacklist"         (process-user-info     request title :blacklist)
            "/all-words/json"         (process-wordlist-json request nil)
            "/all-words/text"         (process-wordlist-text request nil)
            "/all-words/xml"          (process-wordlist-xml  request nil)
            "/all-words/edn"          (process-wordlist-edn  request nil)
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
            ;"/delete-word"           (process-delete-word   request)
            ;"/undelete-word"         (process-undelete-word request)
            )))

