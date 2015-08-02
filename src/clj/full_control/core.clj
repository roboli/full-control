(ns full-control.core
  (:require [clojure.string :as str]
            [om.dom :as dom]))

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags-fns* nil)

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
        [_ dbody :as did-mount] (parse-protocol-fns 'did-mount body)
        [sparams sbody :as should-update] (parse-protocol-fns 'should-update body)
        [wrparams wrbody :as will-receive-props] (parse-protocol-fns 'will-receive-props body)
        [wuparams wubody :as will-update] (parse-protocol-fns 'will-update body)
        [duparams dubody :as did-update] (parse-protocol-fns 'did-update body)
        [rsparams rsbody :as render-state] (parse-protocol-fns 'render-state body)
        [_ dnbody :as display-name] (parse-protocol-fns 'display-name body)
        [_ wumbody :as will-unmount] (parse-protocol-fns 'will-unmount body)]
    (if render-state
      `(defn ~name ~args
         (->Component
          {:init-state-fn (apply (fn ~args
                                   (fn [] ~@(or ibody {})))
                                 ~args)
           
           :will-mount-fn (apply (fn ~args
                                   (fn [] ~@(or wbody [])))
                                 ~args)
           
           :did-mount-fn (apply (fn ~args
                                  (fn [] ~@(or dbody [])))
                                ~args)
           
           :should-update-fn (apply (fn ~args
                                      (fn ~(or sparams []) ~@(or sbody [true])))
                                    ~args)
           
           :will-receive-props-fn (apply (fn ~args
                                           (fn ~(or wrparams []) ~@(or wrbody [])))
                                         ~args)
           
           :will-update-fn (apply (fn ~args
                                    (fn ~(or wuparams []) ~@(or wubody [])))
                                  ~args)
           
           :did-update-fn (apply (fn ~args
                                   (fn ~(or duparams []) ~@(or dubody [])))
                                 ~args)
           
           :render-state-fn (apply (fn ~args
                                     (fn ~(or rsparams []) ~(binding [*tags-fns* tags-fns]
                                                              (apply (tag *tags-fns*) tag rsbody))))
                                   ~args)

           :display-name-fn (apply (fn ~args
                                     (fn [] ~@(or dnbody [])))
                                   ~args)

           :will-unmount-fn (apply (fn ~args
                                     (fn [] ~@(or wumbody [])))
                                   ~args)}))
      (throw (RuntimeException. "No render-state form provided")))))

(defn- gen-fc-mcr
  "Creates a 'defcontrolname' macro to create control as a component."
  [tag]
  `(defmacro ~(symbol (str "def" (name tag))) [name# args# & body#]
     (component '~tag name# args# body#)))

(defmacro gen-fc-mcrs [& {:keys [tags-fns] :or {tags-fns fc-tags-fns}}]
  (cons `do
        (map gen-fc-mcr (keys tags-fns))))

;; Generate macros for all controls
(gen-fc-mcrs)

(defn gen-om-fn
  "Creates wrapper function 'controlname*' for a Om/dom tag"
  [tag]
  (let [t (symbol (str (name tag) "*"))]
    `(defn ~t [attrs# & body#]
       {:pre [(map? attrs#)]}
       (apply ~(symbol (str "om.dom/" tag))
              (cljs.core/clj->js
               (as-> attrs# $#
                     (assoc $# :class-name (utils/general-class-names $# (:class-name $#)))
                     (utils/normalize-attrs $#)))
              body#))))

(defmacro gen-om-fns []
  (cons `do
        (map gen-om-fn om-tags)))

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
  "Returns function 'column-n*' which calls the 'column*' function with its :sizes
  and :cols attributes set to n. See cljs full-control.core/column* for further
  explanation."
  [n]
  `(defn ~(symbol (str "column-" n "*")) [attrs# & body#]
     {:pre [(map? attrs#)]}
     (apply column* (assoc
                        (dissoc attrs# :size :cols)
                      :sizes [{:size (:size attrs#) :cols ~n}])
            body#)))

(defmacro defcolumn
  "Defines a function or functions which returns a column control with its :cols
  attribute set to n which is a number between the start and end parameters. See
  column-defn function."
  [start & [end]]
  (dofun column-defn start end))

(defmacro deflbl-col [start & [end]]
  (dofun
   (fn [n]
     `(defn ~(symbol (str "lbl-" n "*")) [attrs# & body#]
        {:pre [(map? attrs#)]}
        (apply lbl* (assoc attrs# :cols ~n) body#)))
   start end))

(defn- column-control-defn [stag tag n]
  `(defn ~(symbol (str stag n "*")) [attrs# & body#]
     {:pre [(map? attrs#)]}
     (column* {:sizes [(assoc attrs# :cols ~n)]}
              (apply ~tag attrs# body#))))

(defmacro deftxt-col [start & [end]]
  (dofun
   (partial column-control-defn "txt-" 'txt*)
   start end))

(defmacro defpassword-col [start & [end]]
  (dofun
   (partial column-control-defn "password-" 'password*)
   start end))

(defmacro deftxtarea-col [start & [end]]
  (dofun
   (partial column-control-defn "txtarea-" 'txtarea*)
   start end))

(defmacro defhelp-col [start & [end]]
  (dofun
   (partial column-control-defn "help-" 'help*)
   start end))

(defmacro defdropdown-col [start & [end]]
  (dofun
   (partial column-control-defn "dropdown-" 'dropdown*)
   start end))

(defmacro defcheckbox-col [start & [end]]
  (dofun
   (partial column-control-defn "checkbox-" 'checkbox*)
   start end))

(defmacro defdatepicker-col [start & [end]]
  (dofun
   (partial column-control-defn "datepicker-" 'datepicker*)
   start end))
