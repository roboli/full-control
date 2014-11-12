(ns full-control.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defrecord Page [f]
  om/IRenderState
  (render-state [_ state]
    (f state)))

(defn root [f value options]
  (om/root f value options))

(defn page* [attrs & body]
  (apply om.dom/div nil body))

(defn menu-h* [attrs & body]
  (dom/nav #js {:className "navbar navbar-default navbar-static-top"
                :role "navigation"}
           (dom/div #js {:className "container-fluid"}
                    (dom/div #js {:className "navbar-header"}
                             (dom/button #js {:type "button"
                                              :className "navbar-toggle collapsed"
                                              :data-toggle "collapse"
                                              :data-target "#menu-h-collapse-items"}
                                         (dom/span #js {:className "icon-bar"})
                                         (dom/span #js {:className "icon-bar"})
                                         (dom/span #js {:className "icon-bar"}))
                             (dom/a #js {:className "navbar-brand"
                                         :href "#"} (:text attrs)))
                    (apply dom/div #js {:id "menu-h-collapse-items"
                                        :className "collapse navbar-collapse"} body))))

(defn p [attrs value]
  (dom/p nil value))
