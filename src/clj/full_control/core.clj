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

(defn- parse-with-attrs
  "Same as parse-attrs, but assumes the attributes map is within the with-attrs
  form."
  [body & {:keys [not-found]}]
  (if (= (ffirst body) 'with-attrs)
    [(second (first body)) (rest (rest (first body)))]
    [(or not-found {}) body]))

(defn- parse-layout-attrs [body]
  (parse-attrs body :not-found {:column-size :md}))

(defn- parse-column-attrs [body]
  (let [[attrs body] (parse-attrs body)]
    [(assoc attrs :size (:column-size *attrs*)) body]))

;;;
;;; Expanders
;;;

(defn- match-h-name [x]
  (if (re-find #"h[1-5]$" (name x)) 'h))

(defn- match-col-name [x]
  (if (re-find #"column-(?:\d|1[1-2])$" (name x)) 'column-))

(defn- match-title-name [x]
  (if (re-find #"title[1-5]$" (name x)) 'title))

(defn- search-tag-with [& fs]
  (fn [tags tag]
    (if-let [tf (some #(if (not (nil? %)) %)
                      (for [f fs]
                        (f tags tag)))]
      tf)))

(defn- expand-tags
  "Applies f to the *tags* map. f must be a searcher function that expects the
  *tags* map and a tag (symbol) to be search. available and aliases are a set
  and map respectively, their use is to filter and rename keys in the *tags* map
  before making the search. A function is returned which expects a control in
  the form of e.g. (button attrs body), where button is the tag to be searched."
  [f & {:keys [available aliases] :or [aliases {}]}]
  (fn [[tag & body :as form]]
    (-> *tags*
        (select-keys available)
        (clojure.set/rename-keys aliases)
        (#(or (f % tag) (fn [& _] form)))
        (apply tag body))))

(def ^:private expand-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-h-name %2)))))

(def ^:private expand-column-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-col-name %2)))))

(def ^:private expand-panel-header-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-title-name %2)))))

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
  "Expand and transform control's body with the provided functions in the first
  parameter map. Should return the control form as
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
                          :expander (expand-tags-with
                                     :available #{'p 'button 'menu-h 'fixed-layout 'fluid-layout})
                          :transformers []}
         'page body))

;;;
;;; Tags maps
;;;

(def ^:private general-tags #{'p 'button 'h})

(def ^:private general-layout-tags (conj general-tags 'row 'panel))

(def ^:private page-tags
  {;; General
   'p            (partial process-control {:symbol-fn (fn [_] `p*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'button       (partial process-control {:symbol-fn (fn [_] `button*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})

   'h            (partial process-control {:symbol-fn (fn [tag]
                                                        `~(symbol (str "full-control.core/" (name tag) "*")))
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})

   ;; Layout
   'fixed-layout (partial process-control {:symbol-fn (fn [_] `fixed-layout*)
                                           :attrs-parser parse-layout-attrs
                                           :expander (expand-tags-with
                                                      :available general-layout-tags)
                                           :transformers []})
   
   'fluid-layout (partial process-control {:symbol-fn (fn [_] `fluid-layout*)
                                           :attrs-parser parse-layout-attrs
                                           :expander (expand-tags-with
                                                      :available general-layout-tags)
                                           :transformers []})
   
   'row          (partial process-control {:symbol-fn (fn [_] `row*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-column-tags-with
                                                      :available (conj general-tags 'row 'column-))
                                           :transformers []})
   
   'column-      (partial process-control {:symbol-fn (fn [tag]
                                                        `~(symbol (str "full-control.core/" (name tag) "*")))
                                           :attrs-parser parse-column-attrs
                                           :expander (expand-tags-with
                                                      :available general-layout-tags)
                                           :transformers []})

   ;; Menus
   'menu-h       (partial process-control {:symbol-fn (fn [_] `menu-h*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-tags-with
                                                      :available #{'brand 'button-h}
                                                      :aliases {'button-h 'button})
                                           :transformers [(parse-links-h parse-attrs)
                                                          apply-spacers]})

   'brand        (partial process-control {:symbol-fn (fn [_] `brand*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'button-h     (partial process-control {:symbol-fn (fn [_] `menu-h-button*)
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})

   ;; Panels
   'panel        (partial process-control {:symbol-fn (fn [_] `panel*)
                                           :attrs-parser parse-layout-attrs
                                           :expander (expand-tags-with
                                                      :available (conj general-tags 'header 'row 'stretch))
                                           :transformers []})

   'header       (partial process-control {:symbol-fn (fn [_] `panel-header*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-panel-header-tags-with
                                                      :available #{'title})
                                           :transformers []})

   'title        (partial process-control {:symbol-fn (fn [tag]
                                                        `~(symbol (str "full-control.core/" (name tag) "*")))
                                           :attrs-parser parse-attrs
                                           :expander identity
                                           :transformers []})
   
   'stretch      (partial process-control {:symbol-fn (fn [_] `stretch*)
                                           :attrs-parser parse-attrs
                                           :expander (expand-tags-with
                                                      :available (conj general-tags 'row))
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
  {:pre [(and (symbol? name)
              (vector? args))]}
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
