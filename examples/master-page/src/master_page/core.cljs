(ns master-page.core
  (:require [full-control.core :as fc :refer-macros [defpage defpanel]]))

(enable-console-print!)

(def app-state (atom {:brand "Navbar"
                      :panel-1-title "MyPanel-1"
                      :panel-2-title "MyPanel-2"
                      :panel-1-text "Hello panel 1!"
                      :panel-2-text "Hello panel 2!"
                      :navpanel-title "Navpanel"}))

(defpanel panel-1 [cursor owner opts]
  (render-state [st]
                (header (title3 (:panel-1-title cursor)))
                (p (:panel-1-text cursor))))

(defpanel panel-2 [cursor owner opts]
  (render-state [st]
                (header (title3 (:panel-2-title cursor)))
                (p (:panel-2-text cursor))))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand {:href "#"
                                :on-click (fn [_] (js/alert "This is my brand!"))}
                               (:brand cursor))
                        (link {:href "#"
                               :on-click (fn [_] (js/alert "You're home!"))} "Home")
                        (link "About")
                        (spacer)
                        (button {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (row
                    (column-12
                     (fc/build panel-1 cursor)))
                   (row
                    (column-12
                     (fc/build panel-2 cursor ))))
                  (column-3
                   (navpanel (header (title1 (:navpanel-title cursor)))
                             (link {:href "#"
                                    :on-click (fn [_] (js/alert "Uno!"))} "Uno")
                             (link "Dos")))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:texto "Hey you, hey me..."}})
