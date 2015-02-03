(in-ns 'full-control.core)

;;;
;;; Processors
;;;

(declare tags)

(defn- process-control
  [{:keys [symbol-fn attrs-parser expander]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* (symbol-fn tag) attrs (doall (map expander body))))))

(defn- process-with-controls [{:keys [expander]} _ body]
  (let [[symbol attrs & body] body]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* symbol attrs (doall (map expander body))))))

(defn- process-navbar [{:keys [attrs-parser expander]} _ & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (list* `navbar* attrs (->> body
                                 (map expander)
                                 doall
                                 parse-links
                                 apply-spacers)))))

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

(defn- process-form [{:keys [symbol-fn attrs-parser expander]} tag & body]
  (let [[attrs [[_ record & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* (assoc attrs :record record))]
      (list* (symbol-fn tag) attrs (->> body
                                        (map expander)
                                        doall
                                        ;; HACK: must render &nbsp after each
                                        ;; form-group to display correctly
                                        (interpose `nbsp*))))))

(defn- ks->k [korks]
  (if (keyword? korks) korks (last korks)))

(defn- k->ks [k]
  (if (vector? k) k [k]))

(defn- korks-vector [korks]
  [(ks->k korks) (k->ks korks)])

(defn- process-field-label [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [field-key (name (ks->k (:korks *attrs*)))]
        (list (symbol-fn tag) (assoc attrs :html-for field-key)
              (if-not (empty? body) (first body) (str/capitalize field-key)))))))

(defn- on-change-fn
  "Detect if record is cursor or local state, and returns appropiate
  function."
  [r field-ks prop]
  `(fn [v#]
     (let [f# (if (cursor? ~r)
                (partial update! ~r)
                (partial set-state! ~'owner))]
       (f# ~field-ks
           (.. v# ~'-target ~prop)))))

(defn- process-field-checkbox [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [[field-k field-ks] (korks-vector (:korks *attrs*))
            r (gensym "r")]
        `(let ~[r (:record *attrs*)]
           (~(symbol-fn tag) ~(assoc attrs
                                :id (name field-k)
                                :checked (list `get-in r field-ks)
                                :on-change (on-change-fn r field-ks '-checked))
            ~(or (first body) (str/capitalize (name field-k)))))))))

(defn- process-field-text [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [[field-k field-ks] (korks-vector (:korks *attrs*))
            r (gensym "r")]
        `(let ~[r (:record *attrs*)]
           (~(symbol-fn tag) ~(assoc attrs
                                :id (name field-k)
                                :placeholder (if (:inline *attrs*)
                                               (or (:placeholder *attrs*)
                                                   (str/capitalize (name field-k))))
                                :value (list `get-in r field-ks)
                                :on-change (on-change-fn r field-ks '-value))))))))

(defn- process-field-dropdown [{:keys [symbol-fn attrs-parser expander]} tag & body]
  (let [[attrs [[_ [nm coll] & body]]] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [[field-k field-ks] (korks-vector (:korks *attrs*))
            r (gensym "r")]
        `(let ~[r (:record *attrs*)]
           (apply ~(symbol-fn tag) ~(assoc attrs
                                      :id (name field-k)
                                      :value (list `get-in r field-ks)
                                      :on-change (on-change-fn r field-ks '-value))
                  (for [~nm ~coll]
                    ~@(doall (map expander body)))))))))

(defn- process-field-radio [{:keys [symbol-fn attrs-parser]} tag & body]
  (let [[attrs body] (attrs-parser body)]
    (binding [*attrs* (merge *attrs* attrs)]
      (let [[field-k field-ks] (korks-vector (:korks *attrs*))
            r (gensym "r")]
        `(let ~[r (:record *attrs*)]
           (~(symbol-fn tag) ~(assoc attrs
                                :id (name field-k)
                                :name (or (:name attrs) (name field-k))
                                :checked `(= ~(:value attrs) (get-in ~r ~field-ks))
                                :on-change (on-change-fn r field-ks '-value))
            ~(or (first body) (str/capitalize (name field-k)))))))))
