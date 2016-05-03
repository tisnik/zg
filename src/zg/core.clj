(ns zg.core
    "Core module that contains -main function called by Leiningen to start the application.")

(require '[ring.adapter.jetty      :as jetty])
(require '[ring.middleware.params  :as http-params])
(require '[ring.middleware.cookies :as cookies])

(require '[clojure.tools.cli       :as cli])

(require '[zg.server               :as server])
(require '[zg.config               :as config])
(require '[zg.middleware           :as middleware])

(def default-port
    "3000")

(def cli-options
    "Definitions of all command line options currenty supported."
    ;; an option with a required argument
    [["-p" "--port   PORT"    "port number"   :id :port]])

; we need to load the configuration in advance so the 'app' could use it
(def configuration (config/load-configuration "zg.ini"))

(def app
    "Definition of a Ring-based application behaviour."
    (-> server/handler            ; handle all events
        (middleware/inject-configuration configuration) ; inject configuration structure into the parameter
        cookies/wrap-cookies      ; we need to work with cookies
        http-params/wrap-params)) ; and to process request parameters, of course

(defn start-server
    "Start the HTTP server on the specified port."
    [port]
    (println "Starting the server at the port: " port)
    (jetty/run-jetty app {:port (read-string port)}))

(defn get-and-check-port
    "Accepts port number represented by string and throws AssertionError
     if port number is outside defined range."
    [port]
    (let [port-number (. Integer parseInt port)]
        (assert (> port-number 0))
        (assert (< port-number 65536))
        port))

(defn get-port
    "Returns specified port or default port if none is specified on the command line."
    [specified-port]
    (if (or (not specified-port) (not (string? specified-port)) (empty? specified-port))
        default-port
        (get-and-check-port specified-port)))

(defn -main
    "Entry point to the zg server."
    [& args]
    (let [all-options      (cli/parse-opts args cli-options)
          options          (all-options :options)
          port             (options :port)]
          (config/print-configuration configuration)
          (start-server    (get-port port))))

; finito

