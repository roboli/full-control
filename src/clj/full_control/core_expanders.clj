(in-ns 'full-control.core)

;;;
;;; Expanders
;;;

(defn- replace-tag [old new]
  (fn [tag]
    (if (= tag old) new)))

(defn- replace-tag-with-regexp [pattern new-tag tag]
  (if (re-find pattern (name tag)) new-tag))

(def ^:private replace-col-tag
  (partial replace-tag-with-regexp #"column-(?:\d|1[0-2])$" 'column-))

(def ^:private replace-title-tag
  (partial replace-tag-with-regexp #"title[1-5]$" 'title))

(def ^:private replace-lbl-tag
  (partial replace-tag-with-regexp #"lbl-(?:\d|1[0-2])$" 'lbl-))

(def ^:private replace-txt-tag
  (partial replace-tag-with-regexp #"txt-(?:\d|1[0-2])$" 'txt-))

(def ^:private replace-txtarea-tag
  (partial replace-tag-with-regexp #"txtarea-(?:\d|1[0-2])$" 'txtarea-))

(def ^:private replace-dropdown-tag
  (partial replace-tag-with-regexp #"dropdown-(?:\d|1[0-2])$" 'dropdown-))

(def ^:private replace-checkbox-tag
  (partial replace-tag-with-regexp #"checkbox-(?:\d|1[0-2])$" 'checkbox-))

(def ^:private replace-help-tag
  (partial replace-tag-with-regexp #"help-(?:\d|1[0-2])$" 'help-))

(defn- replace-tag-with-fns [fs tag]
  (some #(if-not (nil? %) %)
        ((apply juxt fs) tag)))

(defn- expand-tags-with
  [& {:keys [available alter-tag-fns] :or {alter-tag-fns [identity]}}]
  (fn [[tag & body :as form]]
    (if (symbol? tag)
      (-> (if available (select-keys *tags-fns* available) *tags-fns*)
          (get (or (replace-tag-with-fns alter-tag-fns tag) tag) (fn [& _] form))
          (apply tag body))
      form)))
