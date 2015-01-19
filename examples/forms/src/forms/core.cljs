(ns forms.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:brands [{:id 1 :name "Hermex"}
                               {:id 2 :name "Stanley"}]
                      :item {:description "Screw Driver"
                             :brand-id 2
                             :price 44.5
                             :comments "Yellow color plastic."}}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Forms"))
                (fixed-layout
                 (row
                  (column-9
                   (panel
                    (header (title3 "Normal"))
                    (form
                     (with-record (:item cursor)
                       (row
                        (column-6
                         (group-for :description
                                    (lbl)
                                    (txt {:max-length 15})
                                    (help "*")))
                        (column-6
                         (group-for :price
                                    (lbl)
                                    (txt {:max-length 10})
                                    (help "*"))))
                       (row
                        (column-6
                         (group-for :brand-id
                                    (lbl "Brand")
                                    (dropdown
                                     (with-source [data (:brands cursor)]
                                       (option {:value (:id data)} (:name data))))
                                    (help "*")))
                        (column-6
                         (group-for :comments
                                    (lbl)
                                    (txtarea)
                                    (help "(optional)")))))))))
                 (row
                  (column-9
                   (panel
                    (header (title3 "Horizontal"))
                    (form-horizontal
                     (with-record (:item cursor)
                       (row
                        (column-6
                         (group-for :description
                                    (lbl-4)
                                    (txt-6)
                                    (help-2 "*")))
                        (column-6
                         (group-for :price
                                    (lbl-4)
                                    (txt-6 {:max-length 10})
                                    (help-2 "*"))))
                       (row
                        (column-6
                         (group-for :brand-id
                                    (lbl-4 "Brand")
                                    (dropdown-6
                                     (with-source [data (:brands cursor)]
                                       (option {:value (:id data)} (:name data))))
                                    (help-2 "*")))
                        (column-6
                         (group-for :comments
                                    (lbl-4)
                                    (txtarea-6)
                                    (help-2 "(opt)")))))))))
                 (row
                  (column-9
                   (panel
                    (header (title3 "Inline"))
                    (form-inline
                     (with-record (:item cursor)
                       (group-for :description
                                  (txt {:max-length 15
                                        :placeholder "Name"}))
                       (group-for :price
                                  (txt {:max-length 10}))
                       (group-for :comments
                                  (txt))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
