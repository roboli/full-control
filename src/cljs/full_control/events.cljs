(ns full-control.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [chan put! pub sub]]
            [jayq.core :refer [$]]))

(def ^:private topic-key :event)

(defn init-chans []
  (let [c (chan)]
    {:ch c :pub (pub c topic-key)}))

;;;
;;; Events
;;;

(defn emit [ch m & [f]]
  (put! ch m)
  (if f (f)))

(defn modal-hidden [tag]
  {topic-key tag :show false})

(defn modal-shown [tag]
  {topic-key tag :show true})

(defn modal-on-event [event tag ch id]
  (let [modal-events {:on-show "show.bs.modal"
                      :on-shown "shown.bs.modal"
                      :on-hide "hide.bs.modal"
                      :on-hidden "hidden.bs.modal"
                      :on-loaded "loaded.bs.modal"}]
    (-> ($ js/document)
        (.on (get modal-events event)
             (str "#" id)
             (fn [e]
               (put! ch {topic-key tag
                         :e e
                         :relate-target (-> (. e -relatedTarget)
                                            $
                                            (.attr "id"))}))))))

(defn nav-tab-activated [tag id]
  {topic-key tag :tab-id id})

(defn nav-tab-on-event [event tag ch id]
  (let [tab-events {:on-show "show.bs.tab"
                    :on-shown "shown.bs.tab"
                    :on-hide "hide.bs.tab"
                    :on-hidden "hidden.bs.tab"}]
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
                                            (subs 1))}))))))

;;;
;;; Handlers
;;;

(defn listen [tag pub-ch f]
  (let [c (chan)]
    (sub pub-ch tag c)
    (go (while true
          (f (<! c))))))

(defn modal-display [id]
  (fn [data]
    (if (:show data)
      (-> ($ (str "#" id))
          (.modal #js {:backdrop "static"}))
      (-> ($ (str "#" id))
          (.modal "hide")))))

(defn modal-handler [f]
  (fn [data]
    (f (:e data) (:relate-target data))))

(defn nav-tab-activate [id]
  (fn [data]
    (-> ($ (str "#" id " a[href='#" (:tab-id data) "']"))
        (.tab "show"))))

(defn nav-tab-handler [f]
  (fn [data]
    (f (:e data) (:target data) (:relate-target data))))
