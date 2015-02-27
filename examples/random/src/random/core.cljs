(ns random.core
  (:require [full-control.core :as fc :refer-macros [defpage defpanel]]
            [full-control.behaviors :as b]))

(enable-console-print!)

(def app-state (atom {:document {:date #inst "2015-10-30"}}))

(defpanel date-panel [cursor owner]
  (did-mount []
             (b/jquery-datepicker "dtpk"
                                  :date-format "dd/mm/yy"
                                  :on-select #(fc/transact! cursor [:document :date]
                                                            (fn [_]
                                                              (fc/string->date "dd/MM/yyyy" %)))))
  
  (render-state [st]
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
                  (p (str (get-in cursor [:document :date])))))))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Random")
                        (link "Controls"))
                (fixed-layout
                 (row
                  (column-12
                   (fc/build date-panel cursor))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
