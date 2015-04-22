(defproject full-control "0.1.0-SNAPSHOT"
  :description "A clojurescript Web UI DSL based on Om and Bootstrap."
  :url "http://github.com/roboli/full-control"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2760"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [camel-snake-kebab "0.2.5"]
                 [jayq "2.5.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src/clj" "src/cljs"]

  :cljsbuild  {:builds [; examples
                        {:id "one-page"
                         :source-paths ["src/cljs" "examples/one-page/src"]
                         :compiler {:output-dir "examples/one-page/out"
                                    :output-to "examples/one-page/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main one_page.core
                                    :optimizations :none}}

                        {:id "master-page"
                         :source-paths ["src/cljs" "examples/master-page/src"]
                         :compiler {:output-dir "examples/master-page/out"
                                    :output-to "examples/master-page/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main master_page.core
                                    :optimizations :none}}

                        {:id "grids-tables"
                         :source-paths ["src/cljs" "examples/grids-tables/src"]
                         :compiler {:output-dir "examples/grids-tables/out"
                                    :output-to "examples/grids-tables/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main grids_tables.core
                                    :optimizations :none}}

                        {:id "modals"
                         :source-paths ["src/cljs" "examples/modals/src"]
                         :compiler {:output-dir "examples/modals/out"
                                    :output-to "examples/modals/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main modals.core
                                    :optimizations :none}}

                        {:id "forms"
                         :source-paths ["src/cljs" "examples/forms/src"]
                         :compiler {:output-dir "examples/forms/out"
                                    :output-to "examples/forms/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main forms.core
                                    :optimizations :none}}

                        {:id "tabs"
                         :source-paths ["src/cljs" "examples/tabs/src"]
                         :compiler {:output-dir "examples/tabs/out"
                                    :output-to "examples/tabs/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main tabs.core
                                    :optimizations :none}}

                        {:id "random"
                         :source-paths ["src/cljs" "examples/random/src"]
                         :compiler {:output-dir "examples/random/out"
                                    :output-to "examples/random/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main random.core
                                    :optimizations :none}}

                        {:id "pagers"
                         :source-paths ["src/cljs" "examples/pagers/src"]
                         :compiler {:output-dir "examples/pagers/out"
                                    :output-to "examples/pagers/out/app.js"
                                    :source-map true
                                    :asset-path "out"
                                    :main pagers.core
                                    :optimizations :none}}]}

  :clean-targets ["examples/one-page/out"
                  "examples/master-page/out"
                  "examples/grids-tables/out"
                  "examples/modals/out"
                  "examples/forms/out"
                  "examples/tabs/out"
                  "examples/random/out"
                  "examples/pagers/out"])
