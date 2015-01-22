(in-ns 'full-control.core)

;;;
;;; Expanders
;;;

(defn- match-name [pattern tag s]
  (if (re-find pattern (name s)) tag))

(defn- match-col-name [s]
  (match-name #"column-(?:\d|1[0-2])$" 'column- s))

(defn- match-title-name [s]
  (match-name #"title[1-5]$" 'title s))

(defn- match-lbl-name [s]
  (match-name #"lbl-(?:\d|1[0-2])$" 'lbl- s))

(defn- match-txt-name [s]
  (match-name #"txt-(?:\d|1[0-2])$" 'txt- s))

(defn- match-txtarea-name [s]
  (match-name #"txtarea-(?:\d|1[0-2])$" 'txtarea- s))

(defn- match-dropdown-name [s]
  (match-name #"dropdown-(?:\d|1[0-2])$" 'dropdown- s))

(defn- match-checkbox-name [s]
  (match-name #"checkbox-(?:\d|1[0-2])$" 'checkbox- s))

(defn- match-help-name [s]
  (match-name #"help-(?:\d|1[0-2])$" 'help- s))

(defn- search-tag-with [& fs]
  (fn [tags-fns tag]
    (if-let [f (some #(if-not (nil? %) %)
                     (for [f fs]
                       (f tags-fns tag)))]
      f)))

(defn- expand-tags
  "Applies f to the *tags* map. f must be a searcher function that expects the
  *tags* map and a tag (symbol) to be search. available and aliases are a set
  and map respectively, their use is to filter and rename keys in the *tags* map
  before making the search. A function is returned which expects a control in
  the form of e.g. (button attrs body), where button is the tag to be searched."
  [f & {:keys [available aliases] :or [aliases {}]}]
  (fn [[tag & body :as form]]
    (if (symbol? tag)
      (-> *tags-fns*
          (select-keys (or available (set (keys *tags-fns*))))
          (clojure.set/rename-keys aliases)
          (#(or (f % tag) (fn [& _] form)))
          (apply tag body))
      form)))

(def ^:private expand-tags-with
  (partial expand-tags (search-tag-with (partial get))))

(def ^:private expand-column-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-col-name %2)))))

(def ^:private expand-panel-header-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-title-name %2)))))

(def ^:private expand-group-for-tags-with
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-col-name %2))
                                        #(get %1 (match-lbl-name %2))
                                        #(get %1 (match-txt-name %2))
                                        #(get %1 (match-txtarea-name %2))
                                        #(get %1 (match-dropdown-name %2))
                                        #(get %1 (match-checkbox-name %2))
                                        #(get %1 (match-help-name %2)))))

(def ^:private expand-tags-with-all
  (partial expand-tags (search-tag-with (partial get)
                                        #(get %1 (match-col-name %2))
                                        #(get %1 (match-title-name %2)))))
