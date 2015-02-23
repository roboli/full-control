(ns random.core
  (:require [full-control.core :as fc :refer-macros [defpage]]
            [full-control.behaviors :as b]))

(enable-console-print!)

(def app-state (atom {}))

(defpage page [cursor owner opts]
  (did-mount []
             (b/make-jquery-datepicker "dtpk"))
  
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
                      (form {:class-name "form-horizontal"}
                            (group
                             (lbl-2 "Enter")
                             (datepicker-6 {:id "dtpk"
                                            :value "2015-10-25"})))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
