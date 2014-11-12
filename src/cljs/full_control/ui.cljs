(ns full-control.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defrecord Page [f]
  om/IRenderState
  (render-state [_ state]
    (apply om.dom/div nil (f))))

(defn root [f value options]
  (om/root f value options))

(defn p [attrs value]
  (dom/p nil value))
