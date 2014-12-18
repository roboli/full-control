(ns modals.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan put! pub sub]]
            [jayq.core :refer [$]]
            [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "Modals"
                      :panel-title "Click"}))

(defpage page [cursor owner opts]
  (init-state []
              (let [modal-chan (chan)]
                {:modal-chan modal-chan
                 :modal-pub-chan (pub modal-chan :modal)}))
  
  (will-mount []
              (let [c (chan)]
                (sub (fc/get-state owner :modal-pub-chan) :modal c)
                (go (while true
                      (let [data (<! c)]
                        (if (:show data)
                          (-> ($ (str "#my-modal"))
                              (.modal #js {:backdrop "static"}))
                          (-> ($ (str "#my-modal"))
                              (.modal "hide"))))))))
  
  (render-state [st]
                (fc/modal* {:id "my-modal"}
                           (fc/modal-header* {}
                                             (fc/h4* {} "My Modal"))
                           (fc/p* {} "This is it!!")
                           (fc/modal-footer* {}
                                             (fc/button* {:on-click #(put! (:modal-chan st) {:modal :modal
                                                                                             :show false})}
                                                         "Close")))
                (navbar (brand (:menu-h cursor)))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (button {:on-click #(put! (:modal-chan st) {:modal :modal
                                                                      :show true})}
                                  "Open")))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
