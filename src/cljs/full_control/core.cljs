(ns full-control.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

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

(def ^:private float-class
  {:left "navbar-left"
   :right "navbar-right"})

(defn menu-h*
  "Retuns bootstrap's navbar. Attributes available in the attrs map are
  :brand-class-names, :brand-href, :brand-on-click, :brand-body, :body-class-names."
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
                             (dom/a #js {:className (str "navbar-brand " (:brand-class-names attrs))
                                         :href (:brand-href attrs)
                                         :onClick (:brand-on-click attrs)} (:brand-body attrs)))
                    (apply dom/div #js {:id "menu-h-collapse-items"
                                        :className (str "collapse navbar-collapse ")} body))))

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

(defn menu-h-button*
  "Button to render inside the menu-h control. Attributes available in the attrs
  map same as the button* control."
  [attrs & body]
  (apply button* (assoc attrs
                   :class-names (str "navbar-btn "
                                     (get float-class (:float attrs))
                                     " " (:class-names attrs))
                   :on-click (:on-click attrs)) body))
