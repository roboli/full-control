(in-ns 'full-control.core)

;;;
;;; Processors
;;;

(declare tags)

(defn- process-control
  "Expand and transform control's body with the provided functions in the first
  parameter map. Should return the control form as
  i.e. (fully-qualified/symbol {attrs-map} expanded-transfomred-body)."
  [{:keys [symbol-fn attrs-parser expander transformers]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* (symbol-fn tag) attrs (->> body
                                        (map expander)
                                        doall
                                        ((apply comp (reverse transformers))))))))

(defn- process-children [{:keys [attrs-parser expander]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [body (doall (map expander body))]
        (if attrs
          (list* tag attrs body)
          (list* tag body))))))
