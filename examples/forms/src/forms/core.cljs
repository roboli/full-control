(ns forms.core
  (:require [full-control.core :as fc :refer-macros [defpage deffixed-layout]]))

(enable-console-print!)

(def app-state (atom {:brands [{:id 1 :name "Hermex"}
                               {:id 2 :name "Stanley"}]
                      :item {:description "Screw Driver"
                             :brand-id 2
                             :price 44.5
                             :active true
                             :comments "Yellow color plastic."
                             :extras {:non-taxable false
                                      :allow-credit false
                                      :allow-discounts false}
                             :type "1"}
                      :state {:disabled false}}))

(deffixed-layout layouts [cursor owner opts]
  (render-state [_]
                (row
                 (column-9
                  (panel
                   (header (title3 "Normal"))
                   (frm
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
                                   (help "(optional)"))))
                      (row
                       (column-6
                        (group-for :active
                                   (checkbox))))
                      (row
                       (column-6
                        (lbl "Extras")
                        (checkbox-for [:extras :non-taxable])
                        (checkbox-for [:extras :allow-credit])
                        (checkbox-for [:extras :allow-discounts]))
                       (column-6
                        (lbl "Extras Inline")
                        (br)
                        (checkbox-inline-for [:extras :non-taxable])
                        (checkbox-inline-for [:extras :allow-credit])
                        (checkbox-inline-for [:extras :allow-discounts])))
                      (row
                       (column-6
                        (group-for :type
                                   (lbl)
                                   (radio {:value "1"} "Service")
                                   (radio {:value "2"} "Asset")))
                       (column-6
                        (group-for :type
                                   (lbl "Type Inline")
                                   (br)
                                   (radio-inline {:value "1"
                                                  :name "type1"} "Service")
                                   (radio-inline {:value "2"
                                                  :name "type1"} "Asset")))))))))
                (row
                 (column-9
                  (panel
                   (header (title3 "Horizontal"))
                   (frm-horizontal
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
                                   (help-2 "(opt)"))))
                      (row
                       (column-6
                        (group-for :active
                                   (column-4)
                                   (checkbox-6))))
                      (row
                       (column-6
                        (row
                         (lbl-4 "Extras")
                         (column-6
                          (checkbox-for [:extras :non-taxable])
                          (checkbox-for [:extras :allow-credit])
                          (checkbox-for [:extras :allow-discounts]))))
                       (column-6
                        (row
                         (lbl-4 "Extras Inline")
                         (column-6
                          (checkbox-inline-for [:extras :non-taxable] "NT")
                          (checkbox-inline-for [:extras :allow-credit] "AC")
                          (checkbox-inline-for [:extras :allow-discounts] "AD")))))
                      (row
                       (column-6
                        (group-for :type
                                   (lbl-4)
                                   (column-6
                                    (radio {:value "1"} "Service")
                                    (radio {:value "2"} "Asset"))))
                       (column-6
                        (group-for :type
                                   (lbl-4)
                                   (column-6
                                    (radio-inline {:value "1"
                                                   :name "type1"} "Service")
                                    (radio-inline {:value "2"
                                                   :name "type1"} "Asset"))))))))))
                (row
                 (column-9
                  (panel
                   (header (title3 "Inline"))
                   (row
                    (column-12
                     (frm-inline
                      (with-record (:item cursor)
                        (group-for :description
                                   (txt {:max-length 15
                                         :placeholder "Name"}))
                        (group-for :brand-id
                                   (dropdown
                                    (with-source [data (:brands cursor)]
                                      (option {:value (:id data)} (:name data)))))
                        (group-for :price
                                   (txt {:max-length 10}))
                        (group-for :comments
                                   (txt))))))
                   (row
                    (column-12
                     (frm-inline
                      (with-record (:item cursor)
                        (group-for [:extras :non-taxable]
                                   (checkbox-inline "NT"))
                        (group-for [:extras :allow-credit]
                                   (checkbox-inline "AC"))
                        (group-for [:extras :allow-discounts]
                                   (checkbox-inline "AD"))
                        (space)
                        (space)
                        (group-for :type
                                   (radio-inline {:value "1"} "Service")
                                   (radio-inline {:value "2"} "Asset")))))))))))

(deffixed-layout state [cursor owner opts]
  (init-state []
              {:disabled false
               :description {:val-st nil}
               :price {:val-st nil}
               :brand-id {:val-st nil}
               :comments {:val-st nil}
               :type {:val-st nil}})
  
  (render-state [st]
                (row
                 (column-8
                  (panel
                   (header (title3 "Form"))
                   (frm {:disabled (:disabled st)}
                        (with-record (:item cursor)
                          (row
                           (column-6
                            (group-for {:korks :description
                                        :validation-state (keyword (get-in st [:description :val-st]))}
                                       (lbl)
                                       (txt {:max-length 15})
                                       (help "*")))
                           (column-6
                            (group-for {:korks :price
                                        :validation-state (keyword (get-in st [:price :val-st]))}
                                       (lbl)
                                       (txt {:max-length 10})
                                       (help "*"))))
                          (row
                           (column-6
                            (group-for {:korks :brand-id
                                        :validation-state (keyword (get-in st [:brand-id :val-st]))}
                                       (lbl "Brand")
                                       (dropdown
                                        (with-source [data (:brands cursor)]
                                          (option {:value (:id data)} (:name data))))
                                       (help "*")))
                           (column-6
                            (group-for {:korks :comments
                                        :validation-state (keyword (get-in st [:comments :val-st]))}
                                       (lbl)
                                       (txtarea)
                                       (help "(optional)"))))
                          (row
                           (column-6
                                     (lbl "Extras")
                                     (checkbox-for [:extras :non-taxable])
                                     (checkbox-for [:extras :allow-credit])
                                     (checkbox-for [:extras :allow-discounts]))
                           (column-6
                            (group-for {:korks :type
                                        :validation-state (keyword (get-in st [:type :val-st]))}
                                       (lbl)
                                       (radio {:value "1"} "Service")
                                       (radio {:value "2"} "Asset"))))))))
                 (column-4
                  (panel
                   (header (title3 "Local State"))
                   (with-controls
                     (let [val-sts [{:val nil :txt ""}
                                    {:val :has-success :txt "Success"}
                                    {:val :has-warning :txt "Warning"}
                                    {:val :has-error :txt "Error"}]]
                       (frm-horizontal
                        (with-record st
                          (row
                           (lbl-4 {:size :sm} "Form")
                           (checkbox-6-for {:korks :disabled
                                            :size :sm}))
                          (row
                           (column-12
                            (group-for {:korks [:description :val-st]
                                        :size :sm}
                                       (lbl-4 "Description")
                                       (dropdown-6
                                        (with-source [data val-sts]
                                          (option {:value (:val data)} (:txt data)))))))
                          (row
                           (column-12
                            (group-for {:korks [:price :val-st]
                                        :size :sm}
                                       (lbl-4 "Price")
                                       (dropdown-6
                                        (with-source [data val-sts]
                                          (option {:value (:val data)} (:txt data)))))))
                          (row
                           (column-12
                            (group-for {:korks [:brand-id :val-st]
                                        :size :sm}
                                       (lbl-4 "Brands")
                                       (dropdown-6
                                        (with-source [data val-sts]
                                          (option {:value (:val data)} (:txt data)))))))
                          (row
                           (column-12
                            (group-for {:korks [:comments :val-st]
                                        :size :sm}
                                       (lbl-4 "Comments")
                                       (dropdown-6
                                        (with-source [data val-sts]
                                          (option {:value (:val data)} (:txt data)))))))
                          (row
                           (column-12
                            (group-for {:korks [:type :val-st]
                                        :size :sm}
                                       (lbl-4 "Type")
                                       (dropdown-6
                                        (with-source [data val-sts]
                                          (option {:value (:val data)} (:txt data))))))))))))))))

(defpage page [cursor owner opts]
  (init-state []
              {:section layouts})
  
  (render-state [st]
                (navbar (brand "Forms")
                        (link {:on-click #(fc/set-state! owner :section layouts)
                               :href "#"}
                              "Layouts")
                        (link {:on-click #(fc/set-state! owner :section state)
                               :href "#"}
                              "State"))
                (fc/build (:section st) cursor)))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
