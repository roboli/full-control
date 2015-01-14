(ns full-control.core
  (:require-macros [full-control.core :refer [defcolumn gen-dom-fns deflabel-col]])
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [full-control.utils]))

;;;
;;; Page record and fns
;;;

;; Implements the om.core/IRenderState protocol. Expects m as its constructor
;; parameter. m must be a map with functions as values which returns the body
;; to be used in the protocol's functions.
(defrecord Component [m]
  om/IInitState
  (init-state [_]
    ((:init-state-fn m)))

  om/IWillMount
  (will-mount [_]
    ((:will-mount-fn m)))

  om/IRenderState
  (render-state [_ state]
    ((:render-state-fn m) state)))

(defn root [f value options]
  (om/root f value options))

(defn build
  ([f x] (om/build f x))
  ([f x m] (om/build f x m)))

(defn get-state
  ([owner] (om/get-state owner))
  ([owner korks] (om/get-state owner korks)))

(defn set-state! [owner korks v]
  (om/set-state! owner korks v))

(defn update-state!
  ([owner f] (om/update-state! owner f))
  ([owner korks f] (om/update-state! owner korks f)))

(defn transact!
  ([cursor f] (om/transact! cursor f))
  ([cursor korks f] (om/transact! cursor korks f))
  ([cursor korks f tag] (om/transact! cursor korks f tag)))

(defn update!
  ([cursor v] (om/update! cursor v))
  ([cursor korks v] (om/update! cursor korks v))
  ([cursor korks v tag] (om/update! cursor korks v tag)))

;;;
;;; General controls
;;;

;; All om.dom/tags
(gen-dom-fns)

(defn button*
  "Attributes available in the attrs map are :class-name, :on-click."
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply dom/button #js {:type "button"
                         :className (str "btn btn-default " (:class-name attrs))
                         :onClick (:on-click attrs)} body))

(defn text* [attrs & body]
  {:pre [(map? attrs)]}
  (apply input* (assoc attrs :type "text") body))

(defn page* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* attrs body))

;;;
;;; Layout (Bootstap's grid system)
;;;

(def col-sizes {:xs "xs"
                :sm "sm"
                :md "md"
                :lg "lg"})

(defn fixed-layout* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* {:class-name "container"} body))

(defn fluid-layout* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* {:class-name "container-fluid"} body))

(defn row* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* {:class-name "row"} body))

(defn column*
  "Returns om.dom/div component with its :className set to
  'col-size-n col-size-n ...' where size and n are values in the attrs map.
  attrs must be in the form of

  e.g. {:sizes [{:size :sm :cols 6}
                {:size :md :cols 3}
                ...]}"
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* {:class-name (str/join " " (map
                                          #(str "col-"
                                                (get col-sizes (:size %))
                                                "-"
                                                (:cols %))
                                          (:sizes attrs)))} body))

;; Defines 12 columns controls, column-1* column-2* ... column-12*.
;;
;; e.g. (defn column-7* [attrs & body] ...)
;;
;; Each column maps with bootstrap's grid system columns class names. Attribute
;; available in the attrs map is :size which it can be a value from the
;; col-sizes map. See defcolumn macro in full-control.core clj namespace.
(defcolumn 1 12)

;;;
;;; navbar
;;;

(def ^:private float-class
  {:left "navbar-left"
   :right "navbar-right"})

(defn brand* [attrs & body]
  {:pre [(map? attrs)]}
  {:brand (assoc attrs :body body)})

(defn navbar*
  "Retuns bootstrap's navbar. Attributes available in the attrs map are :class-name."
  [attrs & body]
  {:pre [(map? attrs)]}
  (nav* {:class-name "navbar navbar-default navbar-static-top"
         :role "navigation"}
        (div* {:class-name "container-fluid"}
              (div* {:class-name "navbar-header"}
                    (dom/button #js {:type "button"
                                     :className "navbar-toggle collapsed"
                                     :data-toggle "collapse"
                                     :data-target "#navbar-collapse-items"}
                                (span* {:class-name "icon-bar"})
                                (span* {:class-name "icon-bar"})
                                (span* {:class-name "icon-bar"}))
                    (let [brand (->> body
                                     (filter :brand)
                                     first
                                     :brand)]
                      (apply a* (assoc brand
                                  :class-name (str "navbar-brand "
                                                   (:class-name brand)))
                             (:body brand))))
              (apply div* {:id "navbar-collapse-items"
                           :class-name (str "collapse navbar-collapse "
                                            (:class-name attrs))}
                     (remove :brand body)))))

(defn links-group
  "Returns a series of om.dom/li components inside a om.dom/ul. Basically it
  constructs a menu list from the attrs map parameter. attrs must be in the form of

  e.g. {:links [{:href '#/link1' :body ['link1']}
                {:href '#/link2' :body ['link2' ...] ...}
                ...]}

  Attributes available for each links map are :href, :on-click, :body."
  [attrs]
  {:pre [(map? attrs)]}
  (apply ul* {:class-name (str "nav navbar-nav "
                               (get float-class (:float attrs)))}
         (for [lnk (:links attrs)]
           (li* {}
                (apply a* (dissoc lnk :body) (:body lnk))))))

(defn navbar-button*
  "Button to render inside the navbar control. Attributes available in the attrs
  map same as the button* control."
  [attrs & body]
  {:pre [(map? attrs)]}
  (apply button* (assoc attrs
                   :class-name (str "navbar-btn "
                                    (get float-class (:float attrs))
                                    " " (:class-name attrs))) body))

;;;
;;; Panels
;;;

(defn panel-header* [attrs & body]
  {:pre [(map? attrs)]}
  {:header (assoc attrs :body body)})

(defn stretch* [attrs & body]
  {:pre [(map? attrs)]}
  {:stretch body})

(defn panel* [attrs & body]
  {:pre [(map? attrs)]}
  (div* {:class-name "panel panel-default"}
        (let [header (->> body
                          (filter :header)
                          first
                          :header)]
          (apply div* {:class-name (str "panel-heading " (:class-name header))}
                 (:body header)))
        (if-not (and (= (count body) 1) (:stretch (first body)))
          (apply div* {:class-name "panel-body"} (remove (some-fn :header :stretch) body)))
        (apply div* {} (->> body
                            (filter :stretch)
                            first
                            :stretch))))

(defn navpanel* [attrs & body]
  {:pre [(map? attrs)]}
  (div* {:class-name "panel panel-default"}
        (let [header (->> body
                          (filter :header)
                          first
                          :header)]
          (apply div* {:class-name (str "panel-heading" (:class-name header))}
                 (:body header)))
        (div* {:class-name "panel-body"}
              (apply div* {:class-name "list-group"} (remove :header body)))))

(defn navpanel-link* [attrs & body]
  {:pre [(map? attrs)]}
  (apply a* (assoc attrs :class-name "list-group-item") body))

(defn title1* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h1* (assoc attrs :class-name (str "panel-title " (:class-name attrs)))
         body))

(defn title2* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h2* (assoc attrs :class-name (str "panel-title " (:class-name attrs)))
         body))

(defn title3* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h3* (assoc attrs :class-name (str "panel-title " (:class-name attrs)))
         body))

(defn title4* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h4* (assoc attrs :class-name (str "panel-title " (:class-name attrs)))
         body))

(defn title5* [attrs & body]
  {:pre [(map? attrs)]}
  (apply h5* (assoc attrs :class-name (str "panel-title " (:class-name attrs)))
         body))

;;;
;;; Tables
;;;

(defn grid* [attrs & body]
  {:pre [(map? attrs)]}
  (table* (assoc attrs
            :class-name (str/join " " ["table"
                                       (if (:borders attrs) "table-bordered")
                                       (if (:striped attrs) "table-striped")]))
          (apply tbody* attrs body)))

(defn table* [attrs & body]
  {:pre [(map? attrs)]}
  (apply dom/table #js {:className "table"} body))

;;;
;;; Modals
;;;

(defn modal-header* [attrs & body]
  {:pre [(map? attrs)]}
  {:header (assoc attrs :body body)})

(defn modal-footer* [attrs & body]
  {:pre [(map? attrs)]}
  {:footer (assoc attrs :body body)})

(defn modal* [attrs & body]
  {:pre [(map? attrs)]}
  (div* {:id (:id attrs)
         :class-name "modal fade"
         :role "modal"}
        (div* {:class-name "modal-dialog"}
              (div* {:class-name "modal-content"}
                    (let [header (->> body
                                      (filter :header)
                                      first
                                      :header)]
                      (apply div* {:class-name (str "modal-header " (:class-name header))}
                             (:body header)))
                    (apply div* {:class-name "modal-body"} (remove (some-fn :header :footer) body))
                    (let [footer (->> body
                                      (filter :footer)
                                      first
                                      :footer)]
                      (apply div* {:class-name (str "modal-footer " (:class-name footer))}
                             (:body footer)))))))

;;;
;;; Forms
;;;

(deflabel-col 1 12)

(defn form-text* [attrs & body]
  (apply text* (assoc attrs :class-name "form-control") body))

(defn form-textarea* [attrs & body]
  (apply textarea* (assoc attrs :className "form-control") body))

(defn help* [attrs & body]
  {:pre [(map? attrs)]}
  (apply span* {:class-name "help-block"} body))

(defn form-group* [attrs & body]
  {:pre [(map? attrs)]}
  (apply div* {:class-name "form-group"} body))

(defn form* [attrs & body]
  {:pre [(map? attrs)]}
  (apply dom/form #js {:className (:class-name attrs)} body))

(defn form-horizontal* [attrs & body]
  {:pre [(map? attrs)]}
  (apply form* (assoc attrs :class-name "form-horizontal") body))

(defn form-inline* [attrs & body]
  {:pre [(map? attrs)]}
  (apply form* (assoc attrs :class-name "form-inline") body))
