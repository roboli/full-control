(in-ns 'full-control.core)

;;;
;;; Expanders
;;;

(defn- replace-tag [old new]
  (fn [tag]
    (if (= tag old) new)))

(defn- replace-tag-with-regexp [pattern new-tag tag]
  (if (re-find pattern (name tag)) new-tag))

(def ^:private replace-title-tag
  (partial replace-tag-with-regexp #"title[1-5]$" 'title))

(defn- replace-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"column-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-lbl-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"lbl-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-txt-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"txt-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-txtarea-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"txtarea-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-dropdown-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"dropdown-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-checkbox-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"checkbox-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-datepicker-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"datepicker-(?:\d|1[0-2])$" new-tag tag)))

(defn- replace-help-col-tag-with [new-tag]
  (fn [tag]
    (replace-tag-with-regexp #"help-(?:\d|1[0-2])$" new-tag tag)))

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

(def ^:private row-alter-fns [(replace-col-tag-with 'column-)
                              (replace-lbl-col-tag-with 'lbl-)
                              (replace-txt-col-tag-with 'txt-)
                              (replace-txtarea-col-tag-with 'txtarea-)
                              (replace-dropdown-col-tag-with 'dropdown-)
                              (replace-checkbox-col-tag-with 'checkbox-)
                              (replace-datepicker-col-tag-with 'datepicker-)
                              (replace-help-col-tag-with 'help-)
                              (partial replace-tag-with-regexp #"lbl-(?:\d|1[0-2])-for$" 'lbl--for)
                              (partial replace-tag-with-regexp #"txt-(?:\d|1[0-2])-for$" 'txt--for)
                              (partial replace-tag-with-regexp #"txtarea-(?:\d|1[0-2])-for$" 'txtarea--for)
                              (partial replace-tag-with-regexp #"dropdown-(?:\d|1[0-2])-for$" 'dropdown--for)
                              (partial replace-tag-with-regexp #"checkbox-(?:\d|1[0-2])-for$" 'checkbox--for)])

(def ^:private group-for-alter-fns [(replace-tag 'lbl 'group-lbl)
                                    (replace-tag 'txt 'group-txt)
                                    (replace-tag 'txtarea 'group-txtarea)
                                    (replace-tag 'dropdown 'group-dropdown)
                                    (replace-tag 'checkbox 'group-checkbox)
                                    (replace-tag 'checkbox-inline 'group-checkbox-inline)
                                    (replace-tag 'radio 'group-radio)
                                    (replace-tag 'radio-inline 'group-radio-inline)
                                    (replace-tag 'datepicker 'group-datepicker)
                                    (replace-tag 'help 'group-help)
                                    (replace-col-tag-with 'group-column-)
                                    (replace-lbl-col-tag-with 'group-lbl-)
                                    (replace-txt-col-tag-with 'group-txt-)
                                    (replace-txtarea-col-tag-with 'group-txtarea-)
                                    (replace-dropdown-col-tag-with 'group-dropdown-)
                                    (replace-checkbox-col-tag-with 'group-checkbox-)
                                    (replace-help-col-tag-with 'group-help-)])
