(ns multi-page.core
  (:require [clerk.core :as c :include-macros true :refer [defcom-route defrouter]]
            [full-control.core :as fc :include-macros true :refer [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "MyMenuH"
                      :panel-title "MyPanel"
                      :panel-text "Hello panel!"
                      :menu-v "MyMenuV"}))

(defpage home [cursor owner opts]
  (render-state [st]
                (navbar (brand {:href "#"
                                :on-click (fn [_] (js/alert "This is my brand!"))}
                               (:menu-h cursor))
                        (link {:href "#/"} "Home")
                        (link {:href "#/about"} "About")
                        (spacer)
                        (button {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (p (:panel-text cursor))
                          (stretch
                           (h4 "This is it!!"))))
                  (fc/column-3* {:size :md}
                                (navpanel (header (title1 "Mnu"))
                                          (link {:href "#"
                                                 :on-click (fn [_] (js/alert "Uno!"))} "Uno")
                                          (link "Dos")))))))


(defpage about [cursor owner opts]
  (render-state [st]
                (navbar (brand {:href "#"
                                :on-click (fn [_] (js/alert "This is my brand!"))}
                               (:menu-h cursor))
                        (link {:href "#/"} "Home")
                        (link {:href "#/about"} "About")
                        (spacer)
                        (button {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (h4 (:texto st))))))))

(defcom-route "/" [] home)
(defcom-route "/about" [] about {:state {:texto "This my about!!"}})

(defrouter my-router app-state (. js/document (getElementById "app")))

(c/start my-router "/about")
