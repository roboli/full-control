(ns forms.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:item {:description "Screw Driver"
                             :price 44.5
                             :comments "Yellow color plastic."}}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Forms"))
                (fixed-layout
                 (row
                  (column-9
                   (panel
                    (header (title3 "Fill"))
                    (form {:type :horizontal}
                          (row
                           (column-6
                            (group
                             (label-4 "Descripcion")
                             (text-10 {:value (get-in cursor [:item :description])})
                             (help-2 "*")))
                           (column-6
                            (group
                             (label-4 "Price")
                             (text-10 {:value (get-in cursor [:item :price])})
                             (help-2 "*"))))
                          (row
                           (column-6
                            (group
                             (label-4 "Comments")
                             (textarea-10 {:value (get-in cursor [:item :comments])})
                             (help-2 "(optional)")))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:texto "Hey you, hey me..."}})
