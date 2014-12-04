(defproject full-control "0.1.0-SNAPSHOT"
  :description "A clojurescript Web UI DSL based on Om, Bootstrap and Secretary"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [om "0.7.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild  {:builds [{:id "full-control"
                         :source-paths ["src/cljs"]
                         :compiler {
                                    :output-to "full_control.js"
                                    :output-dir "out"
                                    :optimizations :none
                                    :source-map true}}
                        
                        ; examples
                        {:id "one-page"
                         :source-paths ["src/cljs" "examples/one-page/src"]
                         :compiler {
                                    :output-to "examples/one-page/app.js"
                                    :output-dir "examples/one-page/out"
                                    :source-map "examples/one-page/app.js.map"
                                    :optimizations :none}}]})
