(ns full-control.router
  (:require [secretary.core :as sc]
            [goog.events :as events])
  (:import goog.History
           goog.History.EventType))

(defprotocol IRouter
  (start [app route]))

(deftype Router [cursor target]
  IRouter
  (start [_ route]
    (do (events/listen (doto (History.) (.setEnabled true)) (.-NAVIGATE EventType)
                       #(let [f (sc/dispatch! (.-token %))]
                          (f cursor target)))
        ((sc/dispatch! route) cursor target))))

(defn add-route! [route action]
  (sc/add-route! route action))
