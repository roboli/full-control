(in-ns 'full-control.core)

;;;
;;; Attributes parsers
;;;

(defn- parse-m [body & {:keys [not-found]}]
  (if (map? (first body))
    [(first body) (rest body)]
    (if (= (ffirst body) 'with-attrs)
      [(second (first body)) (rest (rest (first body)))]
      [not-found body])))

(defn- parse-attrs [body]
  (parse-m body :not-found {}))

(defn- parse-layout-attrs [body]
  (parse-m body :not-found {:column-size :md}))

(defn- parse-column-attrs [body]
  (let [[attrs body] (parse-attrs body)]
    [(assoc attrs :size (:column-size *attrs*)) body]))

(defn- parse-group-for-attrs [[m & b]]
  (if (map? m)
    [m b]
    [{:field-key m} b]))
