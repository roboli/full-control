(ns full-control.ui)

(declare page-tags)

(def ^{:dynamic true :private true} *attrs* nil)

(defn- parse-attrs [body]
  (if (map? (first body))
    [(first body) (rest body)]
    [{} body]))

(defn parse-with-attrs [body]
  (if (= (ffirst body) 'with-attrs)
    [(second (first body)) (rest (rest (first body)))]
    [{} body]))

(defn- expand-tags [tags]
  (fn [[tag & body :as form]]
    (apply (get tags tag (fn [& _] form)) body)))

(defn- parse-links-h [attrs-parser]
  (fn [body]
    (->> body
         (partition-by #(= (first %) 'link))
         (map (fn [coll]
                (if (= (ffirst coll) 'link)
                  (list (list 'full-control.ui/links-group {:links (into [] (map #(let [[attrs body] (attrs-parser (rest %))]
                                                                                    (assoc attrs :body (into [] body)))
                                                                                 coll))}))
                  coll)))
         (mapcat identity))))

(defn- apply-spacers [body]
  (if-let [idx (first (keep-indexed #(if (= (first %2) 'spacer) %1) body))]
    (let [[left right] (split-at idx body)
          right (->> right
                     rest
                     (#(conj % '(om.dom/span (js-obj {:className "navbar-right"})))) ; Hack (bootstrap 3.x): prepend empty span so right aligned buttons display margins correctly
                     reverse
                     (map #(if (map? (second %)) (conj (drop 2 %) (assoc (second %) :float :right) (first %)) %)))]
      (concat left right))
    body))

(defn- process-control [-symbol attrs-parser expander transformers & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (conj (->> body
                 (map expander)
                 doall
                 ((apply comp (reverse transformers))))
            attrs -symbol))))

(defn- process-page [body]
  (apply process-control 'full-control.ui/page* parse-with-attrs (expand-tags page-tags) [] body))

(def ^:private menu-h-tags
  {'button (partial process-control 'full-control.ui/menu-h-button* parse-attrs identity [])})

(def ^:private page-tags
  {'menu-h (partial process-control 'full-control.ui/menu-h* parse-attrs (expand-tags menu-h-tags) [(parse-links-h parse-attrs) apply-spacers])
   'p      (partial process-control 'full-control.ui/p* parse-attrs identity [])
   'button (partial process-control 'full-control.ui/button* parse-attrs identity [])})

(defn- parse-render-state [body]
  (let [xs (->> body
                (filter #(= (first %) 'render-state))
                first
                rest)]
    (if-not (empty? xs)
      [(first xs) (rest xs)])))

(defmacro defpage [name args & body]
  (let [[params body :as render-state] (parse-render-state body)]
    (if render-state
      `(defn ~name ~args
         (->Page (apply (fn ~args
                          (fn ~params ~(process-page body)))
                        ~args)))
      (throw (RuntimeException. "No render-state form provided")))))
