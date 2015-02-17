(in-ns 'full-control.core)

;;;
;;; navbar transformers
;;;

(defn- parse-links
  "Group and transform all continuous 'link symbols in a navbar control into a
  links-group control."
  [body]
  (->> body
       (partition-by #(= (first %) 'link))
       (map (fn [coll]
              (if (= (ffirst coll) 'link)
                (list (list 'full-control.core/links-group
                            {:links
                             (vec
                              (map #(let [[attrs body] (parse-attrs (rest %))]
                                      (assoc attrs :body (vec body)))
                                   coll))}))
                coll)))
       (mapcat identity)))

(defn- apply-spacers
  "Float controls to the right side of the spacer. It inserts or updates the
  :float item with :right value in the attributes map of each control."
  [body]
  (if-let [idx (first (keep-indexed #(if (= (first %2) 'spacer) %1) body))]
    (let [[left right] (split-at idx body)
          ;; HACK: (bootstrap 3.x), prepend empty span, so the buttons to the
          ;; right side will display margins correctly
          right (->> right
                     rest
                     (#(conj % '(om.dom/span
                                 (js-obj {:className "navbar-right"}))))
                     reverse
                     (map #(if (map? (second %))
                             (list*
                              (first %) (assoc (second %) :float :right) (drop 2 %))
                             %)))]
      (concat left right))
    body))


;;;
;;; nav-tabs transformers
;;;

(defn- parse-tabs [body expander]
  (let [tabs (keep
              (fn [x]
                (if (= (first x) 'nav-tab)
                  [(second x) (first (filter #(= (first %) 'tab) (drop 2 x)))]))
              body)]
    (list `tab-links-group
          {:links (mapv (fn [[m b]]
                          (let [attrs (if (:active m)
                                        {:li {:class-name "active"}})]
                            (assoc attrs
                              :a {:href (str "#" (:id m))
                                  :body (->> (rest b)
                                             (mapv expander)
                                             doall)})))
                        tabs)})))

(defn- parse-panes [body expander]
  (let [panes (keep
               (fn [x]
                 (if (= (first x) 'nav-tab)
                   [(second x) (first (filter #(= (first %) 'tab-pane) (drop 2 x)))]))
               body)]
    (list `contents-group
          {:contents (mapv (fn [[m b]]
                             (let [attrs (if (:active m)
                                           {:class-name "active"})]
                               (assoc attrs
                                 :id (:id m)
                                 :body (->> (rest b)
                                            (mapv expander)
                                            doall))))
                           panes)})))
