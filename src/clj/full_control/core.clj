(ns full-control.core)

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

(load "core_attr_parsers")
(load "core_expanders")
(load "core_transformers")
(load "core_processors")
(load "core_tags")

;;;
;;; Page macro and fns
;;;

(defn- parse-render-state
  "Find and transform the render-state form inside the page's body."
  [body]
  (let [xs (->> body
                (filter #(= (first %) 'render-state))
                first
                rest)]
    (if (seq xs)
      [(first xs) (rest xs)])))

(defmacro defpage
  "Defines a function which returns an instance of full-control.core/Component record.
  The Component record implements the om.core/IRenderState protocol. See the Component
  record definition in the cljs full-control.core namespace for further explanation."
  [name args & body]
  {:pre [(and (symbol? name)
              (vector? args))]}
  (let [[params body :as render-state] (parse-render-state body)]
    (if render-state
      `(defn ~name ~args
         (->Component (apply (fn ~args
                               (fn ~params ~(binding [*tags* tags]
                                              (apply ('page *tags*) 'page body))))
                             ~args)))
      (throw (RuntimeException. "No render-state form provided")))))

;;;
;;; Layout
;;;

(defn- column-defn
  "Returns form which defines a function that calls the column* control function
  with its :sizes and :cols attributes set to n. See cljs full-control.core/column*
  for further explanation."
  [n]
  `(defn ~(symbol (str "column-" n "*")) [~'attrs & ~'body]
     {:pre [(map? ~'attrs)]}
     (apply full-control.core/column*
            {:sizes [(assoc ~'attrs :cols ~n)]}
            ~'body)))

(defmacro defcolumn
  "Defines a function or functions which returns a column control with its :cols
  attribute set to n which is a number with a value between the start and end
  parameters. See column-defn function."
  [start & [end]]
  (if end
    (cons `do
          (for [n (range start (inc end))]
            (column-defn n)))
    (column-defn start)))
