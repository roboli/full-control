(ns full-control.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan put! pub sub]]
            [jayq.core :refer [$]]))

(def ^:private topic-key :event)

(defn init-chans []
  (let [c (chan)]
    {:ch c :pub (pub c topic-key)}))

(defn listen [tag pub-ch f]
  (let [c (chan)]
    (sub pub-ch tag c)
    (go (while true
          (f (<! c))))))

(defn emit [ch m]
  (fn [& _]
    (put! ch m)))

(defn modal-hide [tag]
  {topic-key tag :show false})

(defn modal-show [tag]
  {topic-key tag :show true})

(defn modal-display [k]
  (fn [data]
    (if (:show data)
      (-> ($ (str "#" (name k)))
          (.modal #js {:backdrop "static"}))
      (-> ($ (str "#" (name k)))
          (.modal "hide")))))
