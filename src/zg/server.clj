(ns zg.server)

(require '[ring.util.response     :as http-response])
(require '[clojure.data.json      :as json])
(require '[clojure.xml            :as xml])

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

(defn get-url-prefix
    [request]
    (-> (:configuration request)
        :server
        :url-prefix))

(defn finish-processing
    [request search-results message]
    (let [params        (:params request)
          cookies       (:cookies request)
          word          (get params "word")
          user-name     (get-user-name request)
          url-prefix    (get-url-prefix request)]
        (println-and-flush "Incoming cookies: " cookies)
        (println word)
        (println search-results)
        (if user-name
            (-> (http-response/response (html-renderer/render-front-page word user-name search-results message url-prefix))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-front-page word user-name search-results message url-prefix))
                (http-response/content-type "text/html")))))

(defn process-front-page
    [request]
    (let [params         (:params request)
          word           (get params "word")
          search-results (if (not (empty? word)) (db-interface/read-words-for-pattern word))]
        (finish-processing request search-results nil)))

(defn store-words
    [words user-name]
    (println "About to store following words:" words)
    (doseq [word words]
        (db-interface/add-new-word-into-dictionary word user-name)))

(defn add-words-message
    [words]
    (if (seq words)
        (str "The following words have been added into the dictionary: '" (clojure.string/join "," words) "'. Thank you!")
        "No words have been added..."))

(defn split-words
    [input]
    (if input
        (->> (clojure.string/split input #"[\s,]")
             (filter seq))))

(defn process-add-words
    [request]
    (let [input     (-> (:params request) (get "new-words"))
          words     (split-words input)
          user-name (get-user-name request)
          message   (add-words-message words)]
          (if (seq words)
              (store-words words user-name))
        (finish-processing request nil message)))

(defn perform-operation
    [request]
    (let [to-delete   (-> (:params request) (get "delete"))
          to-undelete (-> (:params request) (get "undelete"))]
          (println "to delete" to-delete)
          (println "to undelete" to-undelete)
          (if to-delete
              (db-interface/delete-word to-delete))
          (if to-undelete
              (db-interface/undelete-word to-undelete))))

(defn process-all-words
    [request]
    (perform-operation request)
    (let [search-results (db-interface/read-all-words)]
        (finish-processing request search-results nil)))

(defn process-deleted-words
    [request]
    (perform-operation request)
    (let [search-results (db-interface/read-deleted-words)]
        (finish-processing request search-results nil)))

(defn process-active-words
    [request]
    (perform-operation request)
    (let [search-results (db-interface/read-active-words)]
        (finish-processing request search-results nil)))

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
    [request]
    (let [changes-statistic (read-changes-statistic)
          changes           (read-changes)
          user-name         (get-user-name request)
          url-prefix        (get-url-prefix request)]
        (println "stat"    changes-statistic)
        (println "changes" changes)
        (if user-name
            (-> (http-response/response (html-renderer/render-users user-name changes-statistic changes url-prefix))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-users user-name changes-statistic changes url-prefix))
                (http-response/content-type "text/html")))))

(defn process-user-info
    [request]
    (let [params            (:params request)
          selected-user     (get params "name")
          changes           (read-changes-for-user selected-user)
          user-name         (get-user-name request)
          url-prefix        (get-url-prefix request)]
        (if user-name
            (-> (http-response/response (html-renderer/render-user-info selected-user changes url-prefix))
                (http-response/set-cookie :user-name user-name {:max-age 36000000})
                (http-response/content-type "text/html"))
            (-> (http-response/response (html-renderer/render-user-info selected-user changes url-prefix))
                (http-response/content-type "text/html")))))

(defn words->json
    [words]
    (json/write-str words))

(defn words->text
    [words]
    (clojure.string/join "\n" words))

(defn process-wordlist-json
    [request]
    (let [search-results (db-interface/read-all-words)
          json-output    (words->json search-results)]
        (-> (http-response/response json-output)
            (http-response/content-type "application/json"))))

(defn process-wordlist-text
    [request]
    (let [search-results (db-interface/read-all-words)
          text-output    (words->text search-results)]
        (-> (http-response/response text-output)
            (http-response/content-type "text/plain"))))

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
    (let [uri (request :uri)]
        (condp = uri
            "/favicon.ico"       (return-file "favicon.ico" "image/x-icon")
            "/bootstrap.min.css" (return-file "bootstrap.min.css" "text/css")
            "/smearch.css"       (return-file "smearch.css" "text/css")
            "/bootstrap.min.js"  (return-file "bootstrap.min.js" "application/javascript")
            "/"                  (process-front-page request)
            "/all-words"         (process-all-words request)
            "/deleted-words"     (process-deleted-words request)
            "/active-words"      (process-active-words  request)
            ;"/find-words"       (process-find-words    request)
            "/add-words"         (process-add-words     request)
            "/users"             (process-user-list     request)
            "/user"              (process-user-info     request)
            "/wordlist/json"     (process-wordlist-json request)
            "/wordlist/text"     (process-wordlist-text request)
            ;"/delete-word"      (process-delete-word   request)
            ;"/undelete-word"    (process-undelete-word request)
            )))

