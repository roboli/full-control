(ns full-control.ui)

(declare page-tags)

(def ^{:dynamic true :private true} *attrs* nil)

(defn- parse-attrs
  "Parses a control form's body for its attributes. Returns vector with 
  attributes as first value and rest as second value. Empty map is returned when
  no attributes are found."
  [body]
  (if (map? (first body))
    [(first body) (rest body)]
    [{} body]))

(defn parse-with-attrs
  "Same as parse-attrs, but assumes the attributes map is after the 'with-attrs
  symbol."
  [body]
  (if (= (ffirst body) 'with-attrs)
    [(second (first body)) (rest (rest (first body)))]
    [{} body]))

(defn- expand-tags
  "Expects a map which keys are symbols that represents control tags and values
  are functions. Returns f which expects a sequence which is a control form and
  executes the matched function in the tags map against the form."
  [tags]
  (fn [[tag & body :as form]]
    (apply (get tags tag (fn [& _] form)) body)))

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
                  (list (list 'full-control.ui/links-group
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
         'full-control.ui/page*
         parse-with-attrs
         (expand-tags page-tags)
         []
         body))

(def ^:private menu-h-tags
  {'button (partial process-control
                    'full-control.ui/menu-h-button*
                    parse-attrs
                    identity
                    [])})

(def ^:private page-tags
  {'menu-h (partial process-control
                    'full-control.ui/menu-h*
                    parse-attrs
                    (expand-tags menu-h-tags)
                    [(parse-links-h parse-attrs) apply-spacers])
   'p      (partial process-control
                    'full-control.ui/p*
                    parse-attrs
                    identity
                    [])
   'button (partial process-control
                    'full-control.ui/button*
                    parse-attrs
                    identity
                    [])})

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
  "Defines a var which holds an instance of full-control.ui/Page record. The Page
  record implements the om.core/IRenderState protocol. See the Page definition in
  ui.cljs file for further explanation."
  [name args & body]
  (let [[params body :as render-state] (parse-render-state body)]
    (if render-state
      `(defn ~name ~args
         (->Page (apply (fn ~args
                          (fn ~params ~(process-page body)))
                        ~args)))
      (throw (RuntimeException. "No render-state form provided")))))
