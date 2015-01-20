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

(defn- process-grid-view [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs [[_ [name coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      `(apply grid-view* ~attrs (for [~name ~coll]
                                  (tr* {}
                                       (td* {} ~@(doall (map expander body)))))))))

(defn- process-tbody [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs [[_ [name coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      `(apply tbody* ~attrs (for [~name ~coll]
                              (tr* {} ~@(doall (map expander body))))))))

(defn- process-form [{:keys [symbol-fn attrs-parser expander transformers]} tag & body]
  (let [[attrs [[_ cursor & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* (assoc attrs :cursor cursor))]
      (list* (symbol-fn tag) attrs (->> body
                                        (map expander)
                                        doall
                                        ((apply comp (reverse transformers))))))))

(defn- process-form-label [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (name (:field-key *attrs*))]
        (list (symbol-fn tag) (assoc attrs :html-for field-key)
              (if-not (empty? body) (first body) (str/capitalize field-key)))))))

(defn- process-form-text [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (:field-key *attrs*)
            r (gensym "r")]
        `(let ~[r (:cursor *attrs*)]
           (~(symbol-fn tag) ~(assoc attrs
                                :id (name field-key)
                                :placeholder (if (:inline *attrs*)
                                               (or (:placeholder *attrs*)
                                                   (str/capitalize (name field-key))))
                                :value (list `get r field-key)
                                :on-change `(fn [v#]
                                              (update! ~r ~field-key
                                                       (.. v# ~'-target ~'-value))))))))))

(defn- process-form-dropdown [{:keys [symbol-fn attrs-parser expander]} tag & body]
  (let [[attrs [[_ [nm coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (:field-key *attrs*)
            r (gensym "r")]
        `(let ~[r (:cursor *attrs*)]
           (apply ~(symbol-fn tag) ~(assoc attrs
                                      :id (name field-key)
                                      :value (list `get r field-key)
                                      :on-change `(fn [v#]
                                                    (update! ~r ~field-key
                                                             (.. v# ~'-target ~'-value))))
                  (for [~nm ~coll]
                    ~@(doall (map expander body)))))))))

(defn- process-form-checkbox [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (:field-key *attrs*)
            r (gensym "r")]
        `(let ~[r (:cursor *attrs*)]
           (~(symbol-fn tag) ~(assoc attrs
                                :id (name field-key)
                                :checked (list `get r field-key)
                                :on-change `(fn [v#]
                                              (update! ~r ~field-key
                                                       (.. v# ~'-target ~'-checked))))
            ~(or (first body) (str/capitalize (name field-key)))))))))
