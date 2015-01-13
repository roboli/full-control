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

(defn- process-with-controls [{:keys [expander]} _ body]
  (let [[symbol attrs & body] body]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* symbol attrs (doall (map expander body))))))

(defn- process-grid [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs [[_ [name coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      `(apply grid* ~attrs (for [~name ~coll]
                             (tr* {}
                                  (td* {} ~@(doall (map expander body)))))))))

(defn- process-tbody [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs [[_ [name coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      `(apply tbody* ~attrs (for [~name ~coll]
                              (tr* {} ~@(doall (map expander body))))))))

(defn- process-form [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs [[_ cursor & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* (assoc attrs :cursor cursor))]
      (list* `form* attrs (doall (map expander body))))))

(defn- process-form-label [{:keys [attrs-parser]} _ & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (name (:field-key *attrs*))]
        (list `label* (assoc attrs :html-for field-key)
              (if-not (empty? body) body (clojure.string/capitalize field-key)))))))
