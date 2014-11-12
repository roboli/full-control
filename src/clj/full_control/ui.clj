(ns full-control.ui)

(def ^:dynamic *attrs*)

(defn parse-render-state [body]
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
         (->Page (apply (fn ~args (fn ~params (page* nil ~@body))) ~args)))
      (throw (RuntimeException. "No render-state form provided")))))
