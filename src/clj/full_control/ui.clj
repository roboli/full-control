(ns full-control.ui)

(declare page-tags)

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

(defn- parse-attrs [body]
  (if (map? (first body))
    [(first body) (rest body)]
    [nil body]))

(defn parse-with-attrs [body]
  (if (= (ffirst body) 'with-attrs)
    [(second (first body)) (rest (rest (first body)))]
    [nil body]))

(defn- expand-tags [[tag & body :as form]]
  (apply (get *tags* tag (fn [& _] form)) body))

(defn- parse-links-h [body]
  (->> body
       (partition-by #(= (first %) 'link))
       (map #(if (= (ffirst %) 'link)
               (list (list 'full-control.ui/links-group {:links (into [] (mapcat rest %))}))
               %))
       (mapcat identity)))

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
  (let [[attrs body] (attrs-parser body)
        body (if-not (empty? transformers) ((apply comp (reverse transformers)) body) body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (conj (doall (map expander body)) attrs -symbol))))

(def ^:private page-tags
  {'page   (partial process-control 'full-control.ui/page* parse-with-attrs expand-tags [])
   'menu-h (partial process-control 'full-control.ui/menu-h* parse-attrs identity [parse-links-h apply-spacers])
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
                          (fn ~params
                            ~(binding [*tags* page-tags]
                               (expand-tags (cons 'page body)))))
                        ~args)))
      (throw (RuntimeException. "No render-state form provided")))))
