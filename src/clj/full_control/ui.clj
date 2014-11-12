(ns full-control.ui)

(defn- parse-attrs [body]
  (if (map? (first body))
    [(first body) (rest body)]
    [nil body]))

(def ^{:dynamic true :private true} *attrs* nil)
(def ^{:dynamic true :private true} *tags* nil)

(defn- apply-syntax [[tag & body :as form]]
  (apply (get *tags* tag (fn [& _] form)) body))

(declare page-tags)

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

(defn- process-menu-h [processor & body]
  (let [[attrs body] (parse-attrs body)
        body (parse-links-h body)]
    (conj (map processor body) attrs 'full-control.ui/menu-h*)))

(def ^:private page-tags
  {'menu-h (partial process-menu-h apply-syntax)})

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
