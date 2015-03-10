(ns one-page.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "Navbar"
                      :panel-title "Panel"
                      :menu-v "Navpanel"}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand {:href "#"
                                :on-click (fn [_] (js/alert "This is my brand!"))}
                               (:menu-h cursor))
                        (link {:href "#"
                               :on-click (fn [_] (js/alert "You're home!"))} "Home")
                        (link "About")
                        (spacer)
                        (btn {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (p (:text st))))
                  (with-controls
                    (fc/column-3* {:size :md}
                                  (navpanel (header (title1 (:menu-v cursor)))
                                            (link {:href "#"
                                                   :on-click (fn [_] (js/alert "Uno!"))} "Uno")
                                            (link "Dos"))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:text "Hello panel!"}})
