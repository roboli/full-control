(ns tabs.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand "Tabs"))
                (fixed-layout
                 (row
                  (column-12
                   (panel
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
                               (p "Last Tab 3..."))))))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))})
