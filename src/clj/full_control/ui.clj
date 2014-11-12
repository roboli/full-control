(ns full-control.ui)

(def ^:dynamic *attrs*)

(defn parse-render-state [body]
  (if-let [xs (->> body
                   (filter #(= (first %) 'render-state))
                   first
                   rest)]
    [(first xs) (rest xs)]))

(defmacro defpage [name args & body]
  (let [[params body] (parse-render-state body)]
    `(defn ~name ~args
       (->Page (apply (fn ~args (fn ~params (page* nil ~@body))) ~args)))))
