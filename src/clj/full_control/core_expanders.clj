(in-ns 'full-control.core)

;;;
;;; Expanders
;;;

(defn- match-h-name [x]
  (if (re-find #"h[1-5]$" (name x)) 'h))

(defn- match-col-name [x]
  (if (re-find #"column-(?:\d|1[1-2])$" (name x)) 'column-))

(defn- match-title-name [x]
  (if (re-find #"title[1-5]$" (name x)) 'title))

(defn- match-label-name [x]
  (if (re-find #"label-(?:\d|1[0-2])$" (name x)) 'label-))

(defn- search-tag-with [& fs]
  (fn [tags tag]
    (if-let [tf (some #(if (not (nil? %)) %)
                      (for [f fs]
                        (f tags tag)))]
      tf)))

(defn- expand-tags
  "Applies f to the *tags* map. f must be a searcher function that expects the
  *tags* map and a tag (symbol) to be search. available and aliases are a set
  and map respectively, their use is to filter and rename keys in the *tags* map
  before making the search. A function is returned which expects a control in
  the form of e.g. (button attrs body), where button is the tag to be searched."
  [f & {:keys [available aliases] :or [aliases {}]}]
  (fn [[tag & body :as form]]
    (if (symbol? tag)
      (-> *tags*
          (select-keys (or available (set (keys *tags*))))
          (clojure.set/rename-keys aliases)
          (#(or (f % tag) (fn [& _] form)))
          (apply tag body))
      form)))

(def ^:private expand-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-h-name %2)))))

(def ^:private expand-column-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-col-name %2)))))

(def ^:private expand-panel-header-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-title-name %2)))))

(def ^:private expand-group-for-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-label-name %2)))))

(def ^:private expand-tags-with-all
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-h-name %2))
                                        #(get %1 (match-col-name %2))
                                        #(get %1 (match-title-name %2)))))
