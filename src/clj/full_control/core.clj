(ns full-control.core)

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

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

(defn parse-column-attrs [body]
  (let [[attrs body] (parse-attrs body)]
    [(assoc attrs :size (:column-size *attrs*)) body]))

;;;
;;; Expanders
;;;

(defn- match-col-name
  [x]
  (let [s (->> x
               name
               (take 7)
               (apply str))]
    (if (and (not= \* (last (name x)))
             (= "column-" s))
      (symbol s))))

(defn- get-tag [tag tags]
  (get tags tag))

(defn- match-column-tag [tag tags]
  (if-let [s (match-col-name tag)]
    (get tags s)))

(defn- search-tag-with [& fs]
  (fn [tag tags]
    (if-let [tf (some #(if (not (nil? %)) %)
                      (for [f fs]
                        (f tag tags)))]
      tf)))

(defn- expand-tags-with
  [f & {:keys [available aliases] :or [aliases {}]}]
  (fn [[tag & body :as form]]
    (-> *tags*
        (select-keys available)
        (clojure.set/rename-keys aliases)
        (#(or (f tag %) (fn [& _] form)))
        (apply tag body))))

(def ^:private expand-tags-with-get
  (partial expand-tags-with (search-tag-with get-tag)))

(def ^:private expand-tags-with-get-col
  (partial expand-tags-with (search-tag-with get-tag match-column-tag)))

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
;;; Processors
;;;

(declare page-tags)

(defn- process-control
  "Expand and transform control's body with the provided expander and
  transformers. Should return the control form as,
  i.e. (fully-qualified/symbol {attrs-map} expanded-transfomred-body)."
  [{:keys [symbol-fn attrs-parser expander transformers]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* (symbol-fn tag) attrs (->> body
                                        (map expander)
                                        doall
                                        ((apply comp (reverse transformers))))))))

(defn- process-page
  "Begin the expanding and transformation process of the page control."
  [body]
  (apply process-control {:symbol-fn (fn [_] `page*)
                          :attrs-parser parse-with-attrs
                          :expander (expand-tags-with-get
                                     :available #{'p 'button 'menu-h 'fixed-layout 'fluid-layout})
                          :transformers []}
         'page body))

;;;
;;; Tags maps
;;;

(def ^:private page-tags
  {'p            (partial process-control {:symbol-fn (fn [_] `p*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'button       (partial process-control {:symbol-fn (fn [_] `button*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'row          (partial process-control {:symbol-fn (fn [_] `row*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-tags-with-get-col
                                                      :available #{'p 'button 'row 'column-})
                                           :transformers []})
   
   'column-      (partial process-control {:symbol-fn (fn [tag]
                                                        `~(symbol (str "full-control.core/" (name tag) "*")))
                                           :attrs-parser parse-column-attrs
                                           :expander (expand-tags-with-get
                                                      :available #{'p 'button 'row})
                                           :transformers []})
   
   'menu-h       (partial process-control {:symbol-fn (fn [_] `menu-h*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-tags-with-get
                                                      :available #{'button-h}
                                                      :aliases {'button-h 'button})
                                           :transformers [(parse-links-h parse-attrs)
                                                          apply-spacers]})
   
   'button-h     (partial process-control {:symbol-fn (fn [_] `menu-h-button*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'fixed-layout (partial process-control {:symbol-fn (fn [_] `fixed-layout*)
                                           :attrs-parser parse-layout-attrs
                                           :expander (expand-tags-with-get
                                                      :available #{'p 'button 'row})
                                           :transformers []})
   
   'fluid-layout (partial process-control {:symbol-fn (fn [_] `fluid-layout*)
                                           :attrs-parser parse-layout-attrs
                                           :expander (expand-tags-with-get
                                                      :available #{'p 'button 'row})
                                           :transformers []})})


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
                          (fn ~params ~(binding [*tags* page-tags]
                                         (process-page body))))
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
