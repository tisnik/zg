(ns zg.config)

(require '[clojure.pprint :as pprint])

(require '[clojure-ini.core :as clojure-ini])

(defn parse-int
    "Parse the given string as an integer number."
    [string]
    (java.lang.Integer/parseInt string))

(defn parse-float
    "Parse the given string as a float number."
    [string]
    (java.lang.Float/parseFloat string))

(defn load-configuration
    "Load configuration from the provided INI file."
    [ini-file-name]
    (clojure-ini/read-ini ini-file-name :keywordize? true))

(defn print-configuration
    "Print actual configuration to the output."
    [configuration]
    (pprint/pprint configuration))

