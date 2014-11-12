(ns full-control.ui)

(def ^:dynamic *attrs*)

(defmacro defpage [name args & body]
  `(defn ~name ~args
     (let [page# (->Page (apply (fn ~args (fn [] (list ~@body))) ~args))]
       page#)))
