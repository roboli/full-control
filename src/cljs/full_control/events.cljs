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

(defn emit [ch m & [f]]
  (put! ch m)
  (if f (f)))

(defn modal-hide [tag]
  {topic-key tag :show false})

(defn modal-show [tag]
  {topic-key tag :show true})

(defn modal-display [id]
  (fn [data]
    (if (:show data)
      (-> ($ (str "#" id))
          (.modal #js {:backdrop "static"}))
      (-> ($ (str "#" id))
          (.modal "hide")))))

(defn tab-activate [tag id]
  {topic-key tag :tab-id id})

(defn nav-tabs-activate [id]
  (fn [data]
    (-> ($ (str "#" id " a[href='#" (:tab-id data) "']"))
        (.tab "show"))))

(defn tab-on-event [f]
  (fn [data]
    (f (:e data) (:target data) (:relate-target data))))

(def tab-events {:on-show "show.bs.tab"
                 :on-shown "shown.bs.tab"
                 :on-hide "hide.bs.tab"
                 :on-hidden "hidden.bs.tab"})

(defn tab-on-emit [event tag ch id]
  (-> ($ js/document)
      (.on (get tab-events event)
           (str "#" id " a[data-toggle='tab']")
           (fn [e]
             (put! ch {topic-key tag
                       :e e
                       :target (-> (. e -target)
                                   $
                                   (.attr "href")
                                   (subs 1))
                       :relate-target (-> (. e -relatedTarget)
                                          $
                                          (.attr "href")
                                          (subs 1))})))))
