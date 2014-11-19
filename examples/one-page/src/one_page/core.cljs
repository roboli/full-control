(ns one-page.core
  (:require [full-control.core :as fc :include-macros true :refer [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "MyMenuH"
                      :panel-title "MyPanel"
                      :panel-text "Hello panel!"
                      :menu-v "MyMenuV"}))

(defpage page [cursor owner opts]
  (render-state [st]
                (menu-h {:brand-body (:menu-h cursor)}
                        (link {:href "#"
                               :on-click (fn [_] (js/alert "You're home!"))} "Home")
                        (link "About")
                        (spacer)
                        (button {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (panel {:heading-title (:panel-title cursor)}
                          (p (:panel-text cursor))
                          (stretch
                           (p "other..."))))
                  (column-3
                   (button "Button"))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:texto "Hey you, hey me..."}})
