(ns modals.core
  (:require [full-control.core :as fc :refer-macros [defpage]]
            [full-control.events :as e]))

(enable-console-print!)

(def app-state (atom {:menu-h "Modals"
                      :panel-title "Click"}))

(defpage page [cursor owner opts]
  (init-state []
              {:modal-chs (e/init-chans)})

  (will-mount []
              (e/listen :modal
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-display "my-modal")))
  
  (render-state [st]
                (modal {:id "my-modal"}
                       (header (title3 "My Modal"))
                       (p "Hello Modal!")
                       (footer (button {:on-click (e/emit (get-in st [:modal-chs :ch])
                                                          (e/modal-hide :modal))}
                                       "Close")))
                (navbar (brand (:menu-h cursor)))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (button {:on-click (e/emit (get-in st [:modal-chs :ch])
                                                     (e/modal-show :modal))}
                                  "Open")))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
