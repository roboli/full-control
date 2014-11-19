(ns full-control.core
  (:require-macros [full-control.core :refer [defcolumn]])
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

;;;
;;; Page record and fns
;;;

;; Implements the om.core/IRenderState protocol. Expects f as its constructor
;; first parameter. f must be a function which expects a state map and
;; returns the body to be used in the render-state function.
(defrecord Page [f]
  om/IRenderState
  (render-state [_ state]
    (f state)))

(defn root [f value options]
  (om/root f value options))

(defn page* [attrs & body]
  (apply om.dom/div nil body))

;;;
;;; Layout (Bootstap's grid system)
;;;

(def col-sizes {:xs "xs"
                :sm "sm"
                :md "md"
                :lg "lg"})

(defn fixed-layout* [attrs & body]
  (apply dom/div #js {:className "container"} body))

(defn fluid-layout* [attrs & body]
  (apply dom/div #js {:className "container-fluid"} body))

(defn row* [attrs & body]
  (apply dom/div #js {:className "row"} body))

(defn column*
  "Returns om.dom/div component with its :className set to
  'col-size-n col-size-n ...' where size and n are values in the attrs map.
  attrs must be in the form of

  e.g. {:sizes [{:size :sm :cols 6}
                {:size :md :cols 3}
                ...]}"
  [attrs & body]
  (apply dom/div #js {:className (str/join " " (map
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
;;; General controls
;;;

(defn p*
  "Attribute available in the attrs map is :class-names."
  [attrs & body]
  (apply dom/p #js {:className (:class-names attrs)} body))

(defn button*
  "Attributes available in the attrs map are :class-names, :on-click."
  [attrs & body]
  (apply dom/button #js {:type "button"
                         :className (str "btn btn-default " (:class-names attrs))
                         :onClick (:on-click attrs)} body))

(defn h1* [attrs & body]
  (apply dom/h1 #js {:className (:class-names attrs)} body))

(defn h2* [attrs & body]
  (apply dom/h2 #js {:className (:class-names attrs)} body))

(defn h3* [attrs & body]
  (apply dom/h3 #js {:className (:class-names attrs)} body))

(defn h4* [attrs & body]
  (apply dom/h4 #js {:className (:class-names attrs)} body))

(defn h5* [attrs & body]
  (apply dom/h5 #js {:className (:class-names attrs)} body))

;;;
;;; menu-h
;;;

(def ^:private float-class
  {:left "navbar-left"
   :right "navbar-right"})

(defn brand* [attrs & body]
  {:brand {:class-names (:class-names attrs)
           :href (:href attrs)
           :on-click (:on-click attrs)
           :body body}})

(defn menu-h*
  "Retuns bootstrap's navbar. Attributes available in the attrs map are :class-names."
  [attrs & body]
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
                             (let [brand (->> body
                                              (filter :brand)
                                              first
                                              :brand)]
                               (apply dom/a #js {:className (str "navbar-brand "
                                                                 (:class-names brand))
                                                 :href (:href brand)
                                                 :onClick (:on-click brand)}
                                      (:body brand))))
                    (apply dom/div #js {:id "menu-h-collapse-items"
                                        :className (str "collapse navbar-collapse " (:class-names attrs))}
                           (remove :brand body)))))

(defn links-group
  "Returns a series of om.dom/li components inside a om.dom/ul. Basically it
  constructs a menu list from the attrs map parameter. attrs must be in the form of

  e.g. {:links [{:href '#/link1' :body ['link1']}
                {:href '#/link2' :body ['link2' ...] ...}
                ...]}

  Attributes available for each links map are :href, :on-click, :body."
  [attrs]
  (apply dom/ul #js {:className (str "nav navbar-nav "
                                     (get float-class (:float attrs)))}
         (for [lnk (:links attrs)]
           (dom/li nil
                   (apply dom/a #js {:href (:href lnk)
                                     :onClick (:on-click lnk)} (:body lnk))))))

(defn menu-h-button*
  "Button to render inside the menu-h control. Attributes available in the attrs
  map same as the button* control."
  [attrs & body]
  (apply button* (assoc attrs
                   :class-names (str "navbar-btn "
                                     (get float-class (:float attrs))
                                     " " (:class-names attrs))
                   :on-click (:on-click attrs)) body))

;;;
;;; Panels
;;;

(defn panel-header* [attrs & body]
  {:header {:class-names (:class-names attrs)
            :body body}})

(defn stretch* [attrs & body] {:stretch body})

(defn panel* [attrs & body]
  (dom/div #js {:className "panel panel-default"}
           (let [header (->> body
                             (filter :header)
                             first
                             :header)]
             (apply dom/div #js {:className (str "panel-heading " (:class-names header))}
                    (:body header)))
           (if-not (and (= (count body) 1) (:stretch (first body)))
             (apply dom/div #js {:className "panel-body"} (remove (some-fn :header :stretch) body)))
           (apply dom/div nil (->> body
                                   (filter :stretch)
                                   first
                                   :stretch))))

(defn title1* [attrs & body]
  (apply h1* (assoc attrs :class-names (str "panel-title " (:class-names attrs)))
         body))

(defn title2* [attrs & body]
  (apply h2* (assoc attrs :class-names (str "panel-title " (:class-names attrs)))
         body))

(defn title3* [attrs & body]
  (apply h3* (assoc attrs :class-names (str "panel-title " (:class-names attrs)))
         body))

(defn title4* [attrs & body]
  (apply h4* (assoc attrs :class-names (str "panel-title " (:class-names attrs)))
         body))

(defn title5* [attrs & body]
  (apply h5* (assoc attrs :class-names (str "panel-title " (:class-names attrs)))
         body))
