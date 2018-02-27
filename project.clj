(defproject reagent-debugger "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.1"]
                 [http-kit "2.2.0"]
                 [ring/ring-core "1.6.2"]
                 [ring-middleware-format "0.7.2"]
                 ]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :main reagent-debugger.core

  :profiles {:dev
             {:dependencies []

              :plugins      [[lein-figwheel "0.5.10"]]
              }}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljs"]
                :figwheel     {:on-jsload "reagent-debugger.core/reload"}
                :compiler     {:main                 reagent-debugger.core
                               :optimizations        :none
                               :output-to            "resources/public/js/app.js"
                               :output-dir           "resources/public/js/dev"
                               :asset-path           "js/dev"
                               :source-map-timestamp true}}

               {:id           "min"
                :source-paths ["src/cljs"]
                :compiler     {:main            reagent-debugger.core
                               :optimizations   :advanced
                               :output-to       "resources/public/js/app.js"
                               :output-dir      "resources/public/js/min"
                               :elide-asserts   true
                               :closure-defines {goog.DEBUG false}
                               :pretty-print    false}}]})
