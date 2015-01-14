(ns full-control.core
  (:require [om.dom :as dom]))

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

(load "core_attr_parsers")
(load "core_expanders")
(load "core_transformers")
(load "core_processors")
(load "core_tags")

;;;
;;; Components
;;;

(defn- parse-protocol-fns [fn-symbol body]
  (let [xs (->> body
                (filter #(= (first %) fn-symbol))
                first
                rest)]
    (if (seq xs)
      [(first xs) (rest xs)])))

(defn- component
  "Defines a function which returns an instance of full-control.core/Component record.
  The Component record implements the om.core/IRenderState protocol. See the Component
  record definition in the cljs full-control.core namespace for further explanation."
  [tag name args body]
  {:pre [(and (symbol? name)
              (vector? args))]}
  (let [[_ ibody :as init-state] (parse-protocol-fns 'init-state body) 
        [_ wbody :as will-mount] (parse-protocol-fns 'will-mount body)
        [rparams rbody :as render-state] (parse-protocol-fns 'render-state body)]
    (if render-state
      `(defn ~name ~args
         (->Component
          {:init-state-fn (apply (fn ~args
                                   (fn [] ~@(or ibody {})))
                                 ~args)
           :will-mount-fn (apply (fn ~args
                                   (fn [] ~@(or wbody [])))
                                 ~args)
           :render-state-fn (apply (fn ~args
                                     (fn ~rparams ~(binding [*tags* tags]
                                                     (apply (tag *tags*) tag rbody))))
                                   ~args)}))
      (throw (RuntimeException. "No render-state form provided")))))

(defn- gen-com-mcr [tag]
  `(defmacro ~(symbol (str "def" (name tag))) [name# args# & body#]
     (component '~tag name# args# body#)))

(defmacro gen-coms-mcrs [& {:keys [tags] :or {tags com-tags}}]
  (cons `do
        (map gen-com-mcr (keys tags))))

(gen-coms-mcrs)

(defn gen-dom-fn [tag]
  (let [t (symbol (str (name tag) "*"))]
    `(defn ~t [attrs# & body#]
       {:pre [(map? attrs#)]}
       (apply ~(symbol (str "om.dom/" tag))
              (cljs.core/clj->js (full-control.utils/normalize-attrs attrs#))
              body#))))

(defmacro gen-dom-fns []
  (cons `do
        (map gen-dom-fn om-dom-tags)))

;;;
;;; Layout
;;;

(defn- dofun [f start end]
  (if end
    (cons `do
          (for [n (range start (inc end))]
            (f n)))
    (f start)))

(defn- column-defn
  "Returns form which defines a function that calls the column* control function
  with its :sizes and :cols attributes set to n. See cljs full-control.core/column*
  for further explanation."
  [n]
  `(defn ~(symbol (str "column-" n "*")) [~'attrs & ~'body]
     {:pre [(map? ~'attrs)]}
     (apply column* {:sizes [(assoc ~'attrs :cols ~n)]} ~'body)))

(defmacro defcolumn
  "Defines a function or functions which returns a column control with its :cols
  attribute set to n which is a number with a value between the start and end
  parameters. See column-defn function."
  [start & [end]]
  (dofun column-defn start end))

(defn- column-control-defn [stag tag n]
  `(defn ~(symbol (str stag n "*")) [~'attrs & ~'body]
     {:pre [(map? ~'attrs)]}
     (column* {:sizes [(assoc ~'attrs :cols ~n)]}
              (apply ~tag ~'attrs ~'body))))

(defn- column-label-defn [n]
  (column-control-defn "label-" 'label* n))

(defmacro deflabel-col [start & [end]]
  (dofun column-label-defn start end))

(defn- column-text-defn [n]
  (column-control-defn "text-" 'form-text* n))

(defmacro deftext-col [start & [end]]
  (dofun column-text-defn start end))

(defn- column-help-defn [n]
  (column-control-defn "help-" 'help* n))

(defmacro defhelp-col [start & [end]]
  (dofun column-help-defn start end))
