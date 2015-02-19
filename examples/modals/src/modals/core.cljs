(ns modals.core
  (:require [full-control.core :as fc :refer-macros [defpage defmodal]]
            [full-control.events :as e]))

(enable-console-print!)

(def app-state (atom {:menu-h "Modals"
                      :panel-title "Click"
                      :msg ""}))

(defmodal msg-modal [cursor owner opts]
  (render-state [st]
                (with-attrs {:id "msg-modal"}
                  (header (title3 "Msg Modal"))
                  (p "Please click a button: ")
                  (footer (btn {:on-click (fn [_]
                                            (e/emit (:ch opts)
                                                    (e/modal-hidden :modal-msg)
                                                    #(fc/update! cursor :msg "It's okay!")))}
                               "Ok")
                          (btn {:on-click (fn [_]
                                            (e/emit (:ch opts)
                                                    (e/modal-hidden :modal-msg)
                                                    #(fc/update! cursor :msg "Not okay...")))}
                               "Cancel")))))

(defpage page [cursor owner opts]
  (init-state []
              {:modal-chs (e/init-chans)
               :modal-msg-chs (e/init-chans)})

  (will-mount []
              (e/listen :modal
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-display "my-modal"))
              
              (e/listen :modal-msg
                        (fc/get-state owner [:modal-msg-chs :pub])
                        (e/modal-display "msg-modal")))
  
  (render-state [st]
                ;; inline modal
                (modal {:id "my-modal"}
                       (header (title3 "My Modal"))
                       (p "Hello Modal!")
                       (footer (btn {:on-click #(e/emit (get-in st [:modal-chs :ch])
                                                        (e/modal-hidden :modal))}
                                    "Close")))
                ;; component modal
                (fc/build msg-modal cursor {:opts {:ch (get-in st [:modal-msg-chs :ch])}})
                (navbar (brand (:menu-h cursor)))
                (fixed-layout
                 (row
                  (column-12
                   (panel
                    (header (title3 (:panel-title cursor)))
                    (row
                     (column-6
                      (btn {:on-click #(e/emit (get-in st [:modal-chs :ch])
                                               (e/modal-shown :modal))}
                           "Open-1"))
                     (column-6
                      (row
                       (column-6
                        (btn {:on-click #(e/emit (get-in st [:modal-msg-chs :ch])
                                                 (e/modal-shown :modal-msg))}
                             "Open-2"))
                       (column-6
                        (p (str "Response: " (:msg cursor)))))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
