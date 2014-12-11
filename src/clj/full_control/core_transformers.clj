(in-ns 'full-control.core)

;;;
;;; navbar transformers
;;;

(defn- parse-links-h
  "Group and transform all continuous 'link symbols in a navbar control into a
  links-group control. Expects a attributes parser function and returns f that
  expects the body to transform."
  [attrs-parser]
  (fn [body]
    (->> body
         (partition-by #(= (first %) 'link))
         (map (fn [coll]
                (if (= (ffirst coll) 'link)
                  (list (list 'full-control.core/links-group
                              {:links
                               (vec
                                (map #(let [[attrs body] (attrs-parser (rest %))]
                                        (assoc attrs :body (vec body)))
                                     coll))}))
                  coll)))
         (mapcat identity))))

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
