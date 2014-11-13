(ns full-control.ui)

(declare page-tags)

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

(defn- parse-attrs [body]
  (if (map? (first body))
    [(first body) (rest body)]
    [nil body]))

(defn- apply-syntax [[tag & body :as form]]
  (apply (get *tags* tag (fn [& _] form)) body))

(defn- process-page [body]
  (binding [*tags* page-tags]
    (conj (map apply-syntax body) nil 'full-control.ui/page*)))

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
                     (map #(if (map? (second %)) (list (first %) (assoc (second %) :float :right)) %)))]
      (concat left right))
    body))

(defn- process-control [-symbol attrs-parser expander transformers & body]
  (let [[attrs body] (attrs-parser body)
        body ((apply comp (reverse transformers)) body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (conj (map expander body) attrs -symbol))))

(def ^:private page-tags
  {'menu-h (partial process-control 'full-control.ui/menu-h* parse-attrs apply-syntax [parse-links-h apply-spacers])})

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
                            ~(process-page body)))
                        ~args)))
      (throw (RuntimeException. "No render-state form provided")))))
