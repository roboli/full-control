(in-ns 'full-control.core)

;;;
;;; Attributes parsers
;;;

(defn- parse-m [[m & b :as body] & {:keys [not-found]
                                    :or {not-found [nil body]}}]
  (if (map? m)
    [m b]
    (if (and (seq? m) (= (first m) 'with-attrs))
      [(second m) (rest (rest m))]
      not-found)))

(defn- parse-attrs [body]
  (parse-m body :not-found [{} body]))

(defn- parse-layout-attrs [body]
  (parse-m body :not-found [{:column-size :md} body]))

(defn- parse-column-attrs [body]
  (let [[attrs body] (parse-attrs body)]
    [(assoc attrs :size (or (:size attrs) (:column-size *attrs*))) body]))

(defn- parse-group-for-attrs [[m & b :as body]]
  (parse-m body :not-found (if (keyword? m)
                             [{:field-key m} b]
                             [{} body])))

(defn- parse-inline-attrs [body]
  (let [[attrs body] (parse-attrs body)]
    [(assoc attrs :inline true) body]))
