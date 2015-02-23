(ns full-control.behaviors
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

(defn modal-handler [f]
  (fn [data]
    (f (:e data) (:relate-target data))))

(defn nav-tab-handler [f]
  (fn [data]
    (f (:e data) (:target data) (:relate-target data))))

;;;
;;; Methods
;;;

(defn modal-display [action id]
  (if (= action :show)
    (-> ($ (str "#" id))
        (.modal #js {:backdrop "static"}))
    (-> ($ (str "#" id))
        (.modal "hide"))))

(defn nav-tab-activate [id tab-id]
  (-> ($ (str "#" id " a[href='#" tab-id "']"))
      (.tab "show")))

(defn make-jquery-datepicker [id]
  (let [input (.createElement js/document "input")]
    (.setAttribute input "type" "date")
    (if (= (.-type input) "text")
      (-> ($ (str "#" id))
          (.datepicker #js {})))))
