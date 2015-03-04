(ns random.core
  (:require [full-control.core :as fc :refer-macros [defpage defpanel]]
            [full-control.behaviors :as b]))

(enable-console-print!)

(def app-state (atom {:document {:date #inst "2015-10-30"
                                 :city nil}
                      :cities [{:id 1 :name "Guatemala City"}
                               {:id 2 :name "San Jose"}
                               {:id 3 :name "San Salvador"}
                               {:id 4 :name "Panama City"}
                               {:id 5 :name "Tegucigalpa"}
                               {:id 6 :name "Managua"}]}))

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

(defpanel autocomplete-panel [cursor owner]
  (did-mount []
             (b/jquery-autocomplete "city"
                                    (mapv #(assoc % :value (:name %)) (:cities cursor))
                                    (fn [_ ui]
                                      (fc/update! cursor [:document :city] (.. ui -item -id)))
                                    (fn [item]
                                      (str
                                       (fc/render-to-str (fc/a* {} (. item -name)))))))
  
  (render-state [st]
                (header "Autocomplete")
                (row
                 (column-6
                  (form {:class-name "form-horizontal"}
                        (group
                         (lbl-2 "Search")
                         (txt-6 {:id "city"}))))
                 (column-6
                  (p (str "Id: " (get-in cursor [:document :city])))))))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Random")
                        (link "Controls"))
                (fixed-layout
                 (row
                  (column-12
                   (fc/build date-panel cursor)))
                 (row
                  (column-12
                   (fc/build autocomplete-panel cursor))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
