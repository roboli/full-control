(ns full-control.core)

(def ^{:dynamic true :private true} *attrs* nil)

;;;
;;; Attributes parsers
;;;

(defn- parse-attrs
  "Parses a control form's body for its attributes. Returns vector with 
  attributes as first value and rest as second value. Empty map is returned when
  no attributes are found."
  [body & {:keys [not-found]}]
  (if (map? (first body))
    [(first body) (rest body)]
    [(or not-found {}) body]))

(defn parse-with-attrs
  "Same as parse-attrs, but assumes the attributes map is after the 'with-attrs
  symbol."
  [body & {:keys [not-found]}]
  (if (= (ffirst body) 'with-attrs)
    [(second (first body)) (rest (rest (first body)))]
    [(or not-found {}) body]))

(defn parse-layout-attrs [body]
  (parse-attrs body :not-found {:column-size :md}))

;;;
;;; Solely expander
;;;

(defn- expand-tags
  "Expects a map which keys are symbols that represents control tags and values
  are functions. Returns f which expects a sequence which is a control form and
  executes the matched function in the tags map against the form."
  [tags]
  (fn [[tag & body :as form]]
    (apply (get tags tag (fn [& _] form)) body)))

;;;
;;; menu-h transformers
;;;

(defn- parse-links-h
  "Group and transform all continuous 'link symbols in a menu-h control into a
  links-group control. Expects a attributes parser function and returns f that
  expects the body to transform."
  [attrs-parser]
  (fn [body]
    (->> body
         (partition-by #(= (first %) 'link))
         (map (fn [coll]
                (if (= (ffirst coll) 'link)
                  (list (list 'full-control.core/links-group
                              {:links
                               (vec
                                (map #(let [[attrs body] (attrs-parser (rest %))]
                                        (assoc attrs :body (vec body)))
                                     coll))}))
                  coll)))
         (mapcat identity))))

(defn- apply-spacers
  "Float controls to the right side of the spacer. It inserts or updates the
  :float item with :right value in the attributes map of each control."
  [body]
  (if-let [idx (first (keep-indexed #(if (= (first %2) 'spacer) %1) body))]
    (let [[left right] (split-at idx body)
          ;; HACK: (bootstrap 3.x), prepend empty span, so the buttons to the
          ;; right side will display margins correctly
          right (->> right
                     rest
                     (#(conj % '(om.dom/span
                                 (js-obj {:className "navbar-right"}))))
                     reverse
                     (map #(if (map? (second %))
                             (list*
                              (first %) (assoc (second %) :float :right) (drop 2 %))
                             %)))]
      (concat left right))
    body))

;;;
;;; row transformers
;;;

(defn- column-name
  "Change symbol's name from column-n to column-n*"
  [x]
  (let [s (name (first x))]
    (if (and (not= \* (last s))
             (= "column-" (apply str (take 7 s))))
      s)))

(defn- parse-columns
  "Find and expand each tag which matches the value returned from the
  column-name function."
  [attrs-parser]
  (fn [body]
    (doall (map #(if (and (list? %) (symbol? (first %)))
                   (if-let [s (column-name %)]
                     (let [[size rest] (attrs-parser (rest %))]
                       (conj rest
                             {:size (or (:size size) (:column-size *attrs*))}
                             (symbol (str "full-control.core/" s "*"))))
                     %)
                   %)
                body))))

;;;
;;; Processors
;;;

(declare page-tags)

(defn- process-control
  "Expand and transform control's body with the provided expander and
  transformers. Should return the control form as,
  i.e. (fully-qualified/symbol {attrs-map} expanded-transfomred-body)."
  [-symbol attrs-parser expander transformers & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* -symbol attrs (->> body
                                (map expander)
                                doall
                                ((apply comp (reverse transformers))))))))

(defn- process-page
  "Begin the expanding and transformation process of the page control."
  [body]
  (apply process-control
         'full-control.core/page*
         parse-with-attrs
         (expand-tags page-tags)
         []
         body))

;;;
;;; Tags maps
;;;

(def ^:private layout-tags
  {'row (partial process-control
                 'full-control.core/row*
                 parse-attrs
                 identity
                 [(parse-columns parse-attrs)])})

(def ^:private menu-h-tags
  {'button (partial process-control
                    'full-control.core/menu-h-button*
                    parse-attrs
                    identity
                    [])})

(def ^:private page-tags
  {'menu-h       (partial process-control
                          'full-control.core/menu-h*
                          parse-attrs
                          (expand-tags menu-h-tags)
                          [(parse-links-h parse-attrs) apply-spacers])
   'fixed-layout (partial process-control
                          'full-control.core/fixed-layout*
                          parse-layout-attrs
                          (expand-tags layout-tags)
                          [])
   'fluid-layout (partial process-control
                          'full-control.core/fluid-layout*
                          parse-layout-attrs
                          (expand-tags layout-tags)
                          [])
   'p            (partial process-control
                          'full-control.core/p*
                          parse-attrs
                          identity
                          [])
   'button       (partial process-control
                          'full-control.core/button*
                          parse-attrs
                          identity
                          [])})

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
  "Defines a function which returns an instance of full-control.core/Page record.
  The Page record implements the om.core/IRenderState protocol. See the Page
  record definition in the cljs full-control.core namespace for further explanation."
  [name args & body]
  (let [[params body :as render-state] (parse-render-state body)]
    (if render-state
      `(defn ~name ~args
         (->Page (apply (fn ~args
                          (fn ~params ~(process-page body)))
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
