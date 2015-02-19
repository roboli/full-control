(ns tabs.core
  (:require [full-control.core :as fc :refer-macros [defpage defpanel]]
            [full-control.events :as e]))

(enable-console-print!)

(def app-state (atom {:brands [{:id 1 :name "Hermex"}
                               {:id 2 :name "Stanley"}]
                      :item {:description "Screw Driver"
                             :brand-id 2
                             :price 44.5
                             :comments "Yellow color plastic."
                             :extras {:non-taxable false
                                      :allow-credit false
                                      :allow-discounts false}}}))

(defpanel tabs [cursor owner]
  (render-state [st]
                (header "Tabs")
                (nav-tabs
                 (nav-tab {:id "tab-1"}
                          (tab "Tab 1")
                          (tab-pane
                           (row
                            (column-6
                             (p "Tab 1 content goes here..."))
                            (column-6
                             (p "More here!")))))
                 (nav-tab {:id "tab-2"
                           :active true}
                          (tab "Tab 2")
                          (tab-pane
                           (p "Tab 2 goes here...")))
                 (nav-tab {:id "tab-3"}
                          (tab "Tab 3")
                          (tab-pane
                           (p "Last Tab 3..."))))))

(defpanel form-events [cursor owner]
  (init-state []
              {:tabs-chs (e/init-chans)})

  (will-mount []
              (e/listen :tab-activate
                        (fc/get-state owner [:tabs-chs :pub])
                        (e/nav-tab-activate "form-tabs")))
  
  (render-state [st]
                (header "Events")
                (row
                 (column-12
                  (p "Activating tabs using events.")))
                (row
                 (column-6
                  (form {:class-name "form-horizontal"}
                        (group
                         (lbl-1 "Tab")
                         (dropdown-5 {:on-change #(e/emit (get-in st [:tabs-chs :ch])
                                                          (e/nav-tab-activated :tab-activate
                                                                               (.. % -target -value)))
                                      :defaultValue "tab-2"}
                                     (option {:value "tab-1"} "Texts")
                                     (option {:value "tab-2"} "Checkboxes")
                                     (option {:value "tab-3"} "Dropdown"))))))
                (br)
                (frm
                 (with-record (:item cursor)
                   (row
                    (column-12
                     (nav-tabs {:id "form-tabs"}
                               (nav-tab {:id "tab-1"}
                                        (tab "Texts")
                                        (tab-pane
                                         (br)
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
                                           (group-for :comments
                                                      (lbl)
                                                      (txtarea)
                                                      (help "(optional)"))))))
                               (nav-tab {:id "tab-2"
                                         :active true}
                                        (tab "Checkboxes")
                                        (tab-pane
                                         (br)
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
                                           (checkbox-inline-for [:extras :allow-discounts])))))
                               (nav-tab {:id "tab-3"}
                                        (tab "Dropdown")
                                        (tab-pane
                                         (br)
                                         (row
                                          (column-6
                                           (group-for :brand-id
                                                      (lbl "Brand")
                                                      (dropdown
                                                       (with-source [data (:brands cursor)]
                                                         (option {:value (:id data)} (:name data))))
                                                      (help "*")))))))))))))

(defpanel form-state [cursor owner]
  (init-state []
              {:tab-id "tab-2"
               :tabs-chs (e/init-chans)})

  (will-mount []
              (e/listen :tab-show
                        (fc/get-state owner [:tabs-chs :pub])
                        (e/nav-tab-handler #(fc/set-state! owner :tab-id %2)))
              (e/nav-tab-on-event :on-show :tab-show 
                                  (fc/get-state owner [:tabs-chs :ch])
                                  "form-tabs")

              (e/listen :tab-hidden
                        (fc/get-state owner [:tabs-chs :pub])
                        (e/nav-tab-handler #(.log js/console (str "hidden: " %2))))
              (e/nav-tab-on-event :on-hidden :tab-hidden
                                  (fc/get-state owner [:tabs-chs :ch])
                                  "form-tabs"))

  (render-state [st]
                (header "State")
                (row
                 (column-12
                  (p "Activating tabs using state.")))
                (frm-horizontal
                 (with-record st
                   (row
                    (column-6
                     (group-for :tab-id
                                (lbl-1 "Tab")
                                (dropdown-5
                                 (with-source [data [{:id "tab-1" :name "Texts"}
                                                     {:id "tab-2" :name "Checkboxes"}
                                                     {:id "tab-3" :name "Dropdown"}]]
                                   (option {:value (:id data)} (:name data)))))))))
                (br)
                (frm
                 (with-record (:item cursor)
                   (row
                    (column-12
                     (nav-tabs {:id "form-tabs"}
                               (nav-tab {:id "tab-1"
                                         :active (= "tab-1" (:tab-id st))}
                                        (tab "Texts")
                                        (tab-pane
                                         (br)
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
                                           (group-for :comments
                                                      (lbl)
                                                      (txtarea)
                                                      (help "(optional)"))))))
                               (nav-tab {:id "tab-2"
                                         :active (= "tab-2" (:tab-id st))}
                                        (tab "Checkboxes")
                                        (tab-pane
                                         (br)
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
                                           (checkbox-inline-for [:extras :allow-discounts])))))
                               (nav-tab {:id "tab-3"
                                         :active (= "tab-3" (:tab-id st))}
                                        (tab "Dropdown")
                                        (tab-pane
                                         (br)
                                         (row
                                          (column-6
                                           (group-for :brand-id
                                                      (lbl "Brand")
                                                      (dropdown
                                                       (with-source [data (:brands cursor)]
                                                         (option {:value (:id data)} (:name data))))
                                                      (help "*")))))))))))))

(defpage page [cursor owner opts]
  (init-state []
              {:section tabs})
  
  (render-state [st]
                (navbar (brand "Tabs")
                        (link {:on-click #(fc/set-state! owner :section tabs)
                               :href "#"}
                              "Tabs")
                        (link {:on-click #(fc/set-state! owner :section form-events)
                               :href "#"}
                              "Events")
                        (link {:on-click #(fc/set-state! owner :section form-state)
                               :href "#"}
                              "State"))
                (fixed-layout
                 (row
                  (column-12
                   (fc/build (:section st) cursor))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
