(defproject full-control "0.1.0-SNAPSHOT"
  :description "Description temporarily unavailable."
  :url "unavailable"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [om "0.7.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild  {:builds [; examples
                        {:id "one-page"
                         :source-paths ["src/cljs" "examples/one-page/src"]
                         :compiler {:output-dir "examples/one-page/out"
                                    :output-to "examples/one-page/out/app.js"
                                    :source-map "examples/one-page/out/app.js.map"
                                    :optimizations :none}}

                        {:id "master-page"
                         :source-paths ["src/cljs" "examples/master-page/src"]
                         :compiler {:output-dir "examples/master-page/out"
                                    :output-to "examples/master-page/out/app.js"
                                    :source-map "examples/master-page/out/app.js.map"
                                    :optimizations :none}}

                        {:id "grids-tables"
                         :source-paths ["src/cljs" "examples/grids-tables/src"]
                         :compiler {:output-dir "examples/grids-tables/out"
                                    :output-to "examples/grids-tables/out/app.js"
                                    :source-map "examples/grids-tables/out/app.js.map"
                                    :optimizations :none}}]}

  :clean-targets ["examples/one-page/out"
                  "examples/master-page/out"
                  "examples/grids-tables/out"])
