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
;;; Generate attrs
;;;

(declare join-class-names)

(defn generate-attrs [attrs & {:keys [defaults depth target-attrs]
                               :or {defaults {}
                                    target-attrs []}}]
  (let [m (if (empty? depth)
            attrs
            (get-in attrs (into [:children] (interpose :children depth))))
        m (merge (select-keys attrs target-attrs) m)]
    (merge defaults (-> m
                        (assoc
                            :class-name (if (:override-class m)
                                          (:class-name m)
                                          (join-class-names (:class-name defaults)
                                                            (:class-name m))))
                        (dissoc :children)))))

;;;
;;; Generate class names
;;;

(def display {:show "show"
              :invisible "invisible"
              :hidden "hidden"})

(def pull {:left "pull-left"
           :right "pull-right"})

(def sizes {:xs "xs"
            :sm "sm"
            :md "md"
            :lg "lg"})

(def validation-states {:has-success "has-success"
                        :has-warning "has-warning"
                        :has-error "has-error"})

(def navbar-type {:static-top "navbar-static-top"
                  :fixed-top "navbar-fixed-top"
                  :fixed-bottom "navbar-fixed-bottom"})

(def container-type {:fixed "container"
                     :fluid "container-fluid"})

(def float-class {:left "navbar-left"
                  :right "navbar-right"})

(defn display-css [attrs]
  (if (:display attrs) (get display (:display attrs))))

(defn pull-css [attrs]
  (if (:pull attrs) (get pull (:pull attrs))))

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

(defn offset-css [attrs]
  (if (:offset-cols attrs) (str "col-"
                                (get sizes (:size (first (:sizes attrs))))
                                "-offset-"
                                (:offset-cols attrs))))

(defn float-css [attrs]
  (if (:float attrs)
    (get float-class (:float attrs))))

(defn table-borders-css [attrs]
  (if (:borders attrs) "table-bordered"))

(defn table-stripes-css [attrs]
  (if (:striped attrs) "table-striped"))

(defn navbar-type-css [attrs]
  (if (:navbar-type attrs)
    (get navbar-type (:navbar-type attrs))))

(defn container-type-css [attrs]
  (if (:container-type attrs)
    (get container-type (:container-type attrs))
    (:fluid container-type)))

(defn conj-class-names [f attrs & class-names]
  (conj class-names (f attrs)))

(defn join-class-names [& class-names]
  (->> class-names
       (filter (complement nil?))
       (str/join " ")))

(defn general-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names display-css attrs)
       (apply conj-class-names pull-css attrs)
       (apply join-class-names)))

(defn column-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names col-size-css attrs)
       (apply join-class-names)))

(defn input-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names (size-css "input-") attrs)
       (apply join-class-names)))

(defn column-grid-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names val-state-css attrs)
       (apply conj-class-names offset-css attrs)
       (apply join-class-names)))

(defn form-group-class-names [attrs & class-names]
  (->> (conj class-names "form-group")
       (apply conj-class-names (size-css "form-group-") attrs)
       (apply conj-class-names val-state-css attrs)
       (apply join-class-names)))

(defn float-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names float-css attrs)
       (apply join-class-names)))

(defn table-class-names [attrs & class-names]
  (->> (conj class-names "table")
       (apply conj-class-names table-borders-css attrs)
       (apply conj-class-names table-stripes-css attrs)
       (apply join-class-names)))

(defn navbar-class-names [attrs & class-names]
  (->> (conj class-names "navbar navbar-default")
       (apply conj-class-names navbar-type-css attrs)
       (apply join-class-names)))

(defn container-class-names [attrs & class-names]
  (->> class-names
       (apply conj-class-names container-type-css attrs)
       (apply join-class-names)))
