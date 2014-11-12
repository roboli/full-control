(ns full-control.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defrecord Page [f]
  om/IRenderState
  (render-state [_ state]
    (f)))

(defn root [f value options]
  (om/root f value options))

(defn page* [attrs & body]
  (apply om.dom/div nil body))

(defn p [attrs value]
  (dom/p nil value))
