(ns one-page.core
  (:require [full-control.ui :as ui :include-macros true :refer [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "MyMenuH"
                      :panel-title "MyPanel"
                      :panel-text "Hello panel!"
                      :menu-v "MyMenuV"}))

(defpage page [cursor owner opts]
  (render-state [st]
                (menu-h {:text (:menu-h cursor)}
                        (link {:text "Home"})
                        (link {:text "About"})
                        (spacer)
                        (button "Logout"))
                (p (:texto st))
                (p (:panel-text cursor))
                (button "Button")))

(ui/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:texto "Hey you, hey me..."}})
