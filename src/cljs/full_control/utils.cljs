(ns full-control.utils
  (:require [clojure.string :as str]
            [camel-snake-kebab.core :refer [->camelCase]]))

;;;
;;; Normalize attrs
;;;

(def kenab-attrs #{"data" "aria"})

(defn normalize-attrs [attrs]
  (reduce #(let [[k & _] (clojure.string/split (name (key %2)) #"-")]
             (if-not (kenab-attrs k)
               (assoc %1 (->camelCase (key %2)) (val %2))
               (conj %1 %2)))
          {}
          attrs))

;;;
;;; Generate class names
;;;

(def display {:show "show"
              :invisible "invisible"
              :hidden "hidden"})

(def sizes {:xs "xs"
            :sm "sm"
            :md "md"
            :lg "lg"})

(defn display-css [attrs]
  (if (:display attrs) (get display (:display attrs))))

(defn col-size-css [attrs]
  (if (:size attrs) (str "col-"
                         (get sizes (:size attrs))
                         "-"
                         (:cols attrs))))

(defn input-size-css [attrs]
  (if (:size attrs)  (str "input-" (get sizes (:size attrs)))))

(defn conj-class-names [f attrs & class-names]
  (conj class-names (f attrs)))

(defn join-class-names [class-names]
  (->> class-names
       (filter (complement nil?))
       (str/join " ")))

(defn general-css [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       join-class-names))

(defn column-css [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names col-size-css attrs)
       join-class-names))

(defn input-css [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names input-size-css attrs)
       join-class-names))
