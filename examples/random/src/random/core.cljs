(ns random.core
  (:require [full-control.core :as fc :refer-macros [defpage]]
            [full-control.behaviors :as b]))

(enable-console-print!)

(def app-state (atom {:document {:date #inst "2015-10-30"}}))

(defpage page [cursor owner opts]
  (did-mount []
             (b/jquery-datepicker "dtpk"
                                  :date-format "dd/mm/yy"
                                  :on-select #(fc/transact! cursor [:document :date]
                                                            (fn [_]
                                                              (fc/string->date "dd/MM/yyyy" %)))))
  
  (render-state [st]
                (navbar (brand "Random")
                        (link "Controls"))
                (fixed-layout
                 (row
                  (column-12
                   (panel
                    (header "Datepicker")
                    (row
                     (column-6
                      (frm-horizontal
                         (with-record (:document cursor)
                           (row
                            (column-12
                             (group-for :date
                                        (lbl-2)
                                        (datepicker-6 {:id "dtpk"
                                                       :format "dd/MM/yyyy"})))))))
                     (column-6
                      (p (str (get-in cursor [:document :date])))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
