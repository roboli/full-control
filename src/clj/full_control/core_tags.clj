(in-ns 'full-control.core)

;;;
;;; Tags
;;;

(def ^:private om-tags (concat dom/tags '[input textarea option]))

(defn return [x]
  (fn [_] x))

(defn- symbol->qly-symbol [tag]
  (symbol (str "full-control.core/" (name tag) "*")))

(defn- symbol-for->qly-symbol [tag]
  (-> (name tag)
      (str/split #"-")
      drop-last
      (#(str/join "-" %))
      (#(str "full-control.core/" % "*"))
      symbol))

(def ^:private layout-tags '[navbar fixed-layout fluid-layout modal])

(def ^:private row-tags '[with-controls
                          row
                          column-
                          lbl-
                          lbl--for
                          txt-
                          txt--for
                          txtarea-
                          txtarea--for
                          dropdown-
                          dropdown--for
                          checkbox-
                          checkbox--for
                          help-])

(def ^:private form-tags '[lbl-for
                           txt-for
                           txtarea-for
                           dropdown-for
                           checkbox-for
                           checkbox-inline-for])

(def ^:private form-group-tags '[group-column-
                                 group-lbl
                                 group-txt
                                 group-txtarea
                                 group-dropdown
                                 group-checkbox
                                 group-checkbox-inline
                                 group-radio
                                 group-radio-inline
                                 group-help
                                 group-lbl-
                                 group-txt-
                                 group-txtarea-
                                 group-dropdown-
                                 group-checkbox-
                                 group-help-])

(def ^:private general-tags (concat '[with-controls
                                      btn
                                      lbl
                                      txt
                                      txtarea
                                      dropdown
                                      checkbox
                                      checkbox-inline
                                      radio
                                      radio-inline
                                      help
                                      grid-view
                                      grid
                                      form
                                      form-horizontal
                                      form-inline
                                      panel
                                      navpanel] om-tags))

(def ^:private om-tags-fns
  (reduce #(assoc %1
             %2 (partial process-control {:symbol-fn symbol->qly-symbol
                                          :attrs-parser parse-attrs
                                          :expander identity}))
          {}
          om-tags))

(def ^:private fc-tags-fns
  {'with-controls      (partial process-with-controls {:expander (expand-tags-with
                                                                  :alter-tag-fns [(replace-col-tag-with 'column-)])})

   ;; General
   'page               (partial process-control {:symbol-fn (return `page*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available layout-tags)})

   'btn                (partial process-control {:symbol-fn (return `btn*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'lbl                (partial process-control {:symbol-fn (return `label*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'txt                (partial process-control {:symbol-fn (return `txt*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'txtarea            (partial process-control {:symbol-fn (return `txtarea*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'dropdown           (partial process-control {:symbol-fn (return `dropdown*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'option})})

   'checkbox           (partial process-control {:symbol-fn (return `checkbox*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'checkbox-inline    (partial process-control {:symbol-fn (return `checkbox-inline*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'radio              (partial process-control {:symbol-fn (return `radio*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'radio-inline       (partial process-control {:symbol-fn (return `radio-inline*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'help               (partial process-control {:symbol-fn (return `help*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   ;; Layout
   'fixed-layout       (partial process-control {:symbol-fn (return `fixed-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'row})})
   
   'fluid-layout       (partial process-control {:symbol-fn (return `fluid-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'row})})
   
   'row                (partial process-control {:symbol-fn (return `row*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available row-tags
                                                            :alter-tag-fns row-alter-fns)})
   
   'column-            (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        (concat general-tags
                                                                                form-tags)
                                                                        'row
                                                                        'group
                                                                        'group-for))})

   'lbl-               (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})

   'lbl--for           (partial process-field-label {:symbol-fn symbol-for->qly-symbol
                                                     :attrs-parser parse-column-field-attrs
                                                     :expander identity})

   'txt-               (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})

   'txt--for           (partial process-field-text {:symbol-fn symbol-for->qly-symbol
                                                    :attrs-parser parse-column-field-attrs
                                                    :expander identity})

   'txtarea-           (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})

   'txtarea--for       (partial process-field-text {:symbol-fn symbol-for->qly-symbol
                                                    :attrs-parser parse-column-field-attrs
                                                    :expander identity})

   'dropdown-          (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'option})})

   'dropdown--for     (partial process-field-dropdown {:symbol-fn symbol-for->qly-symbol
                                                       :attrs-parser parse-column-field-attrs
                                                       :expander (expand-tags-with
                                                                  :available #{'option})})

   'checkbox-          (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})

   'checkbox--for      (partial process-field-checkbox {:symbol-fn symbol-for->qly-symbol
                                                        :attrs-parser parse-column-field-attrs
                                                        :expander identity})

   'help-              (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})

   ;; Navbar
   'navbar             (partial process-navbar {:attrs-parser parse-attrs
                                                :expander (expand-tags-with
                                                           :available #{'brand 'navbar-btn}
                                                           :alter-tag-fns [(replace-tag 'btn 'navbar-btn)])})

   'brand              (partial process-control {:symbol-fn (return `brand*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})
   
   'navbar-btn         (partial process-control {:symbol-fn (return `navbar-btn*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   ;; Panels
   'panel              (partial process-control {:symbol-fn (return `panel*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'panel-header
                                                                        'row
                                                                        'stretch)
                                                            :alter-tag-fns [(replace-tag 'header 'panel-header)])})

   'panel-header       (partial process-control {:symbol-fn (return `panel-header*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'title}
                                                            :alter-tag-fns [replace-title-tag])})

   'navpanel           (partial process-control {:symbol-fn (return `navpanel*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'panel-header 'link}
                                                            :alter-tag-fns [(replace-tag 'header 'panel-header)])})

   'title              (partial process-control {:symbol-fn symbol->qly-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})
   
   'stretch            (partial process-control {:symbol-fn (return `stretch*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'row))})

   'link               (partial process-control {:symbol-fn (return `navpanel-link*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   ;; Tables
   'grid-view          (partial process-grid-view {:attrs-parser parse-attrs
                                                   :expander (expand-tags-with
                                                              :available (conj
                                                                          general-tags
                                                                          'row))})

   'grid               (partial process-control {:symbol-fn (return `grid*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'thead 'tbody})})

   'thead              (partial process-control {:symbol-fn (return `thead*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'th})})

   'th                 (partial process-control {:symbol-fn (return `th*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available general-tags)})

   'tbody              (partial process-tbody {:attrs-parser parse-attrs
                                               :expander (expand-tags-with
                                                          :attrs-parser #{'td})})

   'td                 (partial process-control {:symbol-fn (return `td*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available general-tags)})

   ;; Modals
   'modal              (partial process-control {:symbol-fn (return `modal*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'modal-header
                                                                        'modal-footer
                                                                        'row)
                                                            :alter-tag-fns [(replace-tag 'header 'modal-header)
                                                                            (replace-tag 'footer 'modal-footer)])})

   'modal-header       (partial process-control {:symbol-fn (return `modal-header*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'title}
                                                            :alter-tag-fns [replace-title-tag])})

   'modal-footer       (partial process-control {:symbol-fn (return `modal-footer*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available general-tags)})

   ;; Forms
   'form               (partial process-form {:symbol-fn (return `form*)
                                              :attrs-parser parse-attrs
                                              :expander (expand-tags-with
                                                         :available #{'row})})

   'form-horizontal    (partial process-form {:symbol-fn (return `form-horizontal*)
                                              :attrs-parser parse-attrs
                                              :expander (expand-tags-with
                                                         :available #{'row})})

   'form-inline        (partial process-form {:symbol-fn (return `form-inline*)
                                              :attrs-parser parse-inline-attrs
                                              :expander (expand-tags-with
                                                         :available #{'group-for})})

   'lbl-for            (partial process-field-label {:symbol-fn (return `label*)
                                                     :attrs-parser parse-field-key-attrs
                                                     :expander identity})

   'txt-for            (partial process-field-text {:symbol-fn (return `txt*)
                                                    :attrs-parser parse-field-key-attrs
                                                    :expander identity})

   'txtarea-for        (partial process-field-text {:symbol-fn (return `txtarea*)
                                                    :attrs-parser parse-field-key-attrs
                                                    :expander identity})

   'dropdown-for       (partial process-field-dropdown {:symbol-fn (return `dropdown*)
                                                        :attrs-parser parse-field-key-attrs
                                                        :expander (expand-tags-with
                                                                   :available #{'option})})

   'checkbox-for       (partial process-field-checkbox {:symbol-fn (return `checkbox*)
                                                        :attrs-parser parse-field-key-attrs})

   'checkbox-inline-for (partial process-field-checkbox {:symbol-fn (return `checkbox-inline*)
                                                         :attrs-parser parse-field-key-attrs})

   'group              (partial process-control {:symbol-fn (return `form-group*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (concat row-tags
                                                                               form-tags)
                                                            :alter-tag-fns row-alter-fns)})

   'group-for          (partial process-control {:symbol-fn (return `form-group*)
                                                 :attrs-parser parse-field-key-attrs
                                                 :expander (expand-tags-with
                                                            :available (concat general-tags
                                                                               form-group-tags)
                                                            :alter-tag-fns group-for-alter-fns)})

   'group-column-       (partial process-control {:symbol-fn symbol->qly-symbol
                                                  :attrs-parser parse-column-attrs
                                                  :expander (expand-tags-with
                                                             :available (concat general-tags
                                                                                form-group-tags)
                                                             :alter-tag-fns group-for-alter-fns)})       

   'group-lbl           (partial process-field-label {:symbol-fn (return `label*)
                                                      :attrs-parser parse-attrs})

   'group-lbl-          (partial process-field-label {:symbol-fn symbol->qly-symbol
                                                      :attrs-parser parse-column-attrs})

   'group-txt           (partial process-field-text {:symbol-fn (return `txt*)
                                                     :attrs-parser parse-attrs})

   'group-txt-          (partial process-field-text {:symbol-fn symbol->qly-symbol
                                                     :attrs-parser parse-column-attrs})

   'group-txtarea       (partial process-field-text {:symbol-fn (return `txtarea*)
                                                     :attrs-parser parse-attrs})

   'group-txtarea-      (partial process-field-text {:symbol-fn symbol->qly-symbol
                                                     :attrs-parser parse-column-attrs})

   'group-dropdown      (partial process-field-dropdown {:symbol-fn (return `dropdown*)
                                                         :attrs-parser parse-attrs
                                                         :expander (expand-tags-with
                                                                    :attrs-parser #{'option})})

   'group-dropdown-     (partial process-field-dropdown {:symbol-fn symbol->qly-symbol
                                                         :attrs-parser parse-column-attrs
                                                         :expander (expand-tags-with
                                                                    :attrs-parser #{'option})})

   'group-checkbox      (partial process-field-checkbox {:symbol-fn (return `checkbox*)
                                                         :attrs-parser parse-attrs})

   'group-checkbox-     (partial process-field-checkbox {:symbol-fn symbol->qly-symbol
                                                         :attrs-parser parse-column-attrs})

   'group-checkbox-inline (partial process-field-checkbox {:symbol-fn (return `checkbox-inline*)
                                                           :attrs-parser parse-attrs})

   'group-radio         (partial process-field-radio {:symbol-fn (return `radio*)
                                                      :attrs-parser parse-attrs})

   'group-radio-inline  (partial process-field-radio {:symbol-fn (return `radio-inline*)
                                                      :attrs-parser parse-attrs})

   'group-help          (partial process-control {:symbol-fn (return `help*)
                                                  :attrs-parser parse-attrs
                                                  :expander identity})

   'group-help-         (partial process-control {:symbol-fn symbol->qly-symbol
                                                  :attrs-parser parse-column-attrs
                                                  :expander identity})})

(def tags-fns (merge om-tags-fns fc-tags-fns))
