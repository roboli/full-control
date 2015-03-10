(ns grids-tables.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:data [{:description "iPod"
                              :price 150
                              :uom "unit"
                              :image-url "ipod.jpeg"}
                             {:description "iMac"
                              :price 999
                              :uom "unit"
                              :image-url "imac.jpeg"}
                             {:description "iPhone"
                              :price 550
                              :uom "unit"
                              :image-url "iphone.jpeg"}]}))

(defpage page [cursor owner opts]
  (render-state [st]
                (fixed-layout
                 (row
                  (column-12
                   (panel
                    (header (title1 "Grid-View"))
                    (p "Some data here...")
                    (stretch
                     (grid-view
                      (with-source [data (:data cursor)]
                        (row
                         (column-12
                          (h3 (:description data))))
                        (row
                         (column-8
                          (row
                           (column-4
                            (label "Price:"))
                           (column-8
                            (p (:price data))))
                          (row
                           (column-4
                            (label "U/M:"))
                           (column-8
                            (p (:uom data)))))
                         (column-4
                          (p (:image-url data))))))))
                   (panel
                    (header (title1 "Grid"))
                    (p "More data here...")
                    (stretch
                     (grid
                      (thead
                       (th "Description")
                       (th "Price")
                       (th "U/M")
                       (th "Image"))
                      (tbody
                       (with-source [data (:data cursor)]
                         (td (:description data))
                         (td (:price data))
                         (td (:uom data))
                         (td (:image-url data))))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
