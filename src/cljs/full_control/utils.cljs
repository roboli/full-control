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

(def validation-states {:has-success "has-success"
                        :has-warning "has-warning"
                        :has-error "has-error"})

(defn display-css [attrs]
  (if (:display attrs) (get display (:display attrs))))

(defn size-css [s]
  (fn [attrs]
    (if (:size attrs)  (str s (get sizes (:size attrs))))))

(defn col-size-css [attrs]
  (if (:size attrs) (str "col-"
                         (get sizes (:size attrs))
                         "-"
                         (:cols attrs))))

(defn val-state-css [attrs]
  (if (:validation-state attrs)
    (get validation-states (:validation-state attrs))))

(defn conj-class-names [f attrs & class-names]
  (conj class-names (f attrs)))

(defn join-class-names [& class-names]
  (->> class-names
       (filter (complement nil?))
       (str/join " ")))

(defn general-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply join-class-names)))

(defn column-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names col-size-css attrs)
       (apply join-class-names)))

(defn input-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names (size-css "input-") attrs)
       (apply join-class-names)))

(defn validation-state-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names val-state-css attrs)
       (apply join-class-names)))

(defn form-group-class-names [attrs & class-names]
  (->> (conj class-names "form-group")
       (apply conj-class-names display-css attrs)
       (apply conj-class-names (size-css "form-group-") attrs)
       (apply conj-class-names val-state-css attrs)
       (apply join-class-names)))
