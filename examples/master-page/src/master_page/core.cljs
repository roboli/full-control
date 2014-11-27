(ns master-page.core
  (:require [full-control.core :as fc :include-macros true :refer [defpage]]
            [full-control.router :as rt :include-macros true :refer [defpage-route defrouter]]))

(enable-console-print!)

(def app-state (atom {:menu-h "MyMenuH"
                      :panel-title "MyPanel"
                      :panel-text "Hello panel!"
                      :menu-v "MyMenuV"}))

(defpage page [cursor owner opts]
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
                          (p (:texto st))))
                  (column-3
                   (navpanel (header (title1 "Mnu"))
                             (link {:href "#"
                                    :on-click (fn [_] (js/alert "Uno!"))} "Uno")
                             (link "Dos")))))))

(defpage-route "/" [] page {:state {:texto "This is home..."}})
(defpage-route "/about" [] page {:state {:texto "This about..."}})

(defrouter my-router app-state (. js/document (getElementById "app")))

(rt/start my-router "/about")
