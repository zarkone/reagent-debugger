(ns reagent-debugger.core
  (:require
   [reagent.core :as reagent]
   [cljs.reader :refer [read-string]]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom {}))

(defonce variables-atom (reagent/atom ""))

(defn connect-ws! [uri {:keys [on-open on-message on-close]}]
  (let [ws (js/WebSocket. uri)]
    (doto ws
      (aset "onopen" #(on-open ws %))
      (aset "onmessage" #(on-message ws %))
      (aset "onclose" #(on-close ws %)))))

(defn variable-component [variable]
  [:li
   [:span (str variable)]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [ratom]
  [:div
   [:h1 "Vars"]
   [:ul
    (when-not (empty? @variables-atom)
      (doall
       (for [var (read-string @variables-atom)]
         ^{:key (str var)}
         [variable-component var])))]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defonce connections-atom (reagent/atom []))

(defn reload []
  (.log js/console (str @connections-atom))

  (doseq [c @connections-atom]
    (.close c))

  (swap! connections-atom conj
         (connect-ws! "ws://localhost:8000/ws"
                      {:on-open #(.log js/console (str "opened!"))
                       :on-close #(.log js/console (str "closeed!"))
                       :on-message (fn [_ e]
                                     (reset! variables-atom (.-data e))
                                     (.dir js/console (.-data e)))}))

  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
