(ns modals.core
  (:require [full-control.core :as fc :refer-macros [defpage
                                                     defpanel
                                                     defmodal]]
            [full-control.events :as e]))

(enable-console-print!)

(def app-state (atom {:msg ""}))

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

(defpanel modal-events [cursor owner]
  (init-state []
              {:modal-chs (e/init-chans)
               :event ""})

  (will-mount []
              (e/listen :modal
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-display "my-modal"))

              (e/listen :show
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-handler (fn [& _]
                                           (fc/update-state! owner :event #(str % " Show")))))
              (e/modal-on-event :on-show :show
                                (fc/get-state owner [:modal-chs :ch])
                                "my-modal")

              
              (e/listen :shown
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-handler (fn [& _]
                                           (fc/update-state! owner :event #(str % " Shown")))))
              (e/modal-on-event :on-shown :shown
                                (fc/get-state owner [:modal-chs :ch])
                                "my-modal")

              (e/listen :hide
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-handler (fn [& _]
                                           (fc/update-state! owner :event #(str % " Hide")))))
              (e/modal-on-event :on-hide :hide
                                (fc/get-state owner [:modal-chs :ch])
                                "my-modal")
              
              (e/listen :hidden
                        (fc/get-state owner [:modal-chs :pub])
                        (e/modal-handler (fn [& _]
                                           (fc/update-state! owner :event #(str % " Hidden")))))
              (e/modal-on-event :on-hidden :hidden
                                (fc/get-state owner [:modal-chs :ch])
                                "my-modal"))

  (render-state [st]
                (modal {:id "my-modal"}
                       (header (title3 "My Modal"))
                       (p "Hello Modal!")
                       (footer (btn {:on-click #(e/emit (get-in st [:modal-chs :ch])
                                                        (e/modal-hidden :modal))}
                                    "Close")))
                (header (title3 "Events"))
                (row
                 (column-2
                  (btn {:on-click #(e/emit (get-in st [:modal-chs :ch])
                                           (e/modal-shown :modal))}
                       "Open"))
                 (column-10
                  (p (:event st))))))

(defpanel modal-methods [cursor owner]
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
                (header (title3 "Methods"))
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
                    (p (str "Response: " (:msg cursor)))))))))

(defpage page [cursor owner]
  (init-state []
              {:section modal-methods})
  
  (render-state [st]
                (navbar (brand "Modals")
                        (link {:on-click #(fc/set-state! owner :section modal-methods)
                               :href "#"}
                              "Methods")
                        (link {:on-click #(fc/set-state! owner :section modal-events)
                                 :href "#"}
                                "Events"))
                (fixed-layout
                 (row
                  (column-12
                   (fc/build (:section st) cursor))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
