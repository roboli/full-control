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
      [nil body])))

(defn- default-value [vl v]
  (if-not (first v) (assoc v 0 vl) v))

(def ^:private default-empty
  (partial default-value {}))

(def ^:private default-col-size
  (partial default-value {:column-size :md}))

(defn- merge-size [[m b]]
  [(assoc m :size (or (:size m) (:column-size *attrs*))) b])

(defn- merge-inline [[m b]]
  [(assoc m :inline true) b])

(defn- parse-field-key [[m & b :as body]]
  (if (keyword? m) (cons {:field-key m} b) body))

(defn- parse-attrs [body]
  ((comp default-empty parse-m) body))

(defn- parse-layout-attrs [body]
  ((comp default-col-size parse-m) body))

(defn- parse-column-attrs [body]
  ((comp merge-size default-empty parse-m) body))

(defn- parse-group-for-attrs [body]
  ((comp default-empty parse-m parse-field-key) body))

(defn- parse-inline-attrs [body]
  ((comp merge-inline default-empty parse-m) body))
