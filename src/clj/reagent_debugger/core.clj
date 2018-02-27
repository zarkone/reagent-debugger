(ns reagent-debugger.core
  (:gen-class)
  (:require [org.httpkit.server :as httpkit-server]
            [ring.middleware.params]
            [ring.middleware.file :refer [wrap-file]]))

(def channels (atom []))

@channels
(def my-webh
  (-> (fn [req]
        (cond
          (= (:uri req) "/ws")
          (org.httpkit.server/with-channel req channel
            (do (println "on open")
                (swap! channels conj channel)
                )
            (do (println "on close")
                )
            (do (println "on message")
                )
            )
          :else                 {:status  200
                                 :headers {"Content-type" "text/plain"}
                                 :body    "error not found server.web.main: "}))
      (ring.middleware.params/wrap-params)
      (wrap-file "resources/public/")))

(defn launch-dev []
  (println "launch-dev")
  (org.httpkit.server/run-server
   #'my-webh
   {:port  8000
    :join? true}))

(defn broadcast! [data]
  (doseq [ch @channels]
    (httpkit-server/send! ch data)))

#_(broadcast! (str [{:foo :bar} 1 2]))

(defn -main [& args]
  (launch-dev))
