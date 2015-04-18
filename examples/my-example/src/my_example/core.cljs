(ns my-example.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(def app-state (atom {}))

(defpage home [cursor owner]
  (render-state [_]
                (navbar (brand "MyExample")
                        (link {:href "#"} "Home"))
                (fixed-layout
                 (row
                  (column-12
                   (jumbotron
                    (h1 "Welcome!")
                    (p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec ut nisi id turpis interdum cursus.")))))))

(fc/root
 home
 app-state
 {:target (. js/document (getElementById "app"))})
