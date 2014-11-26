(ns full-control.router)

(defmacro defpage-route
  "Simplified version of secretary.core/defroute macro."
  [route destruct component & opts]
  `(let [action# (fn [params#]
                   (cond
                    (map? params#)
                    (let [~(if (vector? destruct)
                             {:keys destruct}
                             destruct) params#]
                      (fn [cursor# target#]
                        (om.core/root ~component cursor# {:target target#
                                                          :opts ~@(or opts (list {}))})))
                    (vector? params#)
                    (let [~destruct params#]
                      (fn [cursor# target#]
                        (om.core/root ~component cursor# {:target target#
                                                          :opts ~@(or opts (list {}))})))))]
     (full-control.router/add-route! ~route action#)))

(defmacro defrouter [name cursor target]
  `(def ~name (full-control.router/->Router ~cursor ~target)))
