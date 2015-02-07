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

(defn- display-css [attrs & class-names]
  (if (:display attrs)
    (conj class-names (get display (:display attrs)))
    class-names))

(defn- general-css [attrs & class-names]
  (->> class-names
       (apply display-css attrs)
       (filter (complement nil?))
       (str/join " ")))
