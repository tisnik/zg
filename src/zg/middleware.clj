(ns zg.middleware)

(defn inject-configuration
    "Inject configuration structure into the request parameter."
    [handler configuration]
    (fn [request]
        (handler (assoc request :configuration configuration))))

