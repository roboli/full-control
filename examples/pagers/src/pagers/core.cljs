(ns pagers.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:cities [{:id 1 :name "Guatemala City"}
                               {:id 2 :name "San Jose"}
                               {:id 3 :name "San Salvador"}
                               {:id 4 :name "Panama City"}
                               {:id 5 :name "Tegucigalpa"}
                               {:id 6 :name "Managua"}
                               {:id 7 :name "Belmopan"}
                               {:id 8 :name "Mexico City"}
                               {:id 9 :name "Bogota"}
                               {:id 10 :name "Quito"}
                               {:id 11 :name "Caracas"}
                               {:id 12 :name "Lima"}
                               {:id 13 :name "Sucre"}]
                      :pagination {:page 1
                                   :page-size 10
                                   :total-pages 2
                                   :total-records 13}}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Pagers"))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header "Data")
                          (stretch
                           (grid
                            (thead
                             (th "Id")
                             (th "City"))
                            (tbody
                             (source [data (let [page (get-in cursor [:pagination :page])
                                                 page-size (get-in cursor [:pagination :page-size])]
                                             (take page-size
                                                   (drop (* (dec page) page-size)
                                                         (:cities cursor))))]
                                     (td (:id data))
                                     (td (:name data)))))
                           (space)
                           (space)
                           (pager {:page (get-in cursor [:pagination :page])
                                   :page-size (get-in cursor [:pagination :page-size])
                                   :total-pages (get-in cursor [:pagination :total-pages])
                                   :pager-size 2
                                   :page-sizes [5 10 15]
                                   :on-page-changed #(fc/update! cursor [:pagination :page] %)
                                   :on-page-size-changed (fn [v]
                                                           (let [pagination (:pagination @cursor)
                                                                 total-pages (Math/ceil (/ (:total-records pagination) v))
                                                                 page (:page pagination)]
                                                             (fc/update! cursor [:pagination :page-size] v)
                                                             (fc/update! cursor [:pagination :total-pages] total-pages)
                                                             (fc/update! cursor
                                                                         [:pagination :page]
                                                                         (if (> page total-pages) total-pages page))))}))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
