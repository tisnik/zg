(ns zg.server)

(require '[ring.util.response     :as http-response])

(require '[zg.db-interface        :as db-interface])
(require '[zg.html-renderer       :as html-renderer])

(defn println-and-flush
    "Original (println) has problem with syncing when it's called from more threads.
     This function is a bit better because it flushes all output immediatelly."
    [& more]
    (.write *out* (str (clojure.string/join " " more) "\n"))
    (flush))

(defn process-front-page
    [request]
    (let [word (-> (:params request) (get "word"))
          search-results (if (not (empty? word)) (db-interface/read-words-for-pattern word))]
        (println word)
        (println search-results)
        (-> (http-response/response (html-renderer/render-front-page word search-results))
            (http-response/content-type "text/html"))))

(defn store-words
    [words]
    (println "About to store following words:" words)
    (doseq [word words]
        (db-interface/add-new-word-into-dictionary word)))

(defn process-add-words
    [request]
    (let [input (-> (:params request) (get "new-words"))
          words (if input (clojure.string/split input #"[\s,]"))]
          (if words
              (store-words words))
        (-> (http-response/response (html-renderer/render-front-page nil nil))
            (http-response/content-type "text/html"))))

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
        (println search-results)
        (-> (http-response/response (html-renderer/render-front-page nil search-results))
            (http-response/content-type "text/html"))))

(defn process-deleted-words
    [request]
    (perform-operation request)
    (let [search-results (db-interface/read-deleted-words)]
        (println search-results)
        (-> (http-response/response (html-renderer/render-front-page nil search-results))
            (http-response/content-type "text/html"))))

(defn process-active-words
    [request]
    (perform-operation request)
    (let [search-results (db-interface/read-active-words)]
        (println search-results)
        (-> (http-response/response (html-renderer/render-front-page nil search-results))
            (http-response/content-type "text/html"))))

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
            ;"/find-words"        (process-find-words    request)
            "/add-words"         (process-add-words     request)
            ;"/delete-word"       (process-delete-word   request)
            ;"/undelete-word"     (process-undelete-word request)
            )))

