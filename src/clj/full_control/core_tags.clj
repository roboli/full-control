(in-ns 'full-control.core)

;;;
;;; Tags
;;;

(def ^:private om-dom-tags (into dom/tags '[input textarea option]))

(defn return [x]
  (fn [_] x))

(defn- tag->qualilified-symbol [tag]
  `~(symbol (str "full-control.core/" (name tag) "*")))

(def ^:private general-tags (into #{'with-controls
                                    'btn
                                    'txt
                                    'txtarea
                                    'dropdown
                                    'checkbox
                                    'checkbox-inline
                                    'checkbox-for
                                    'radio
                                    'radio-inline
                                    'grid-view
                                    'grid
                                    'modal
                                    'form
                                    'form-horizontal
                                    'form-inline} om-dom-tags))

(def ^:private general-layout-tags (conj general-tags 'row 'panel 'navpanel))

(def ^:private dom-tags
  (reduce #(assoc %1
             %2 (partial process-control {:symbol-fn tag->qualilified-symbol
                                          :attrs-parser parse-attrs
                                          :expander identity}))
          {}
          om-dom-tags))

(def ^:private com-tags
  {'with-controls      (partial process-with-controls {:expander (expand-tags-with-all)})

   ;; General
   'page               (partial process-control {:symbol-fn (return `page*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-layout-tags
                                                                        'navbar
                                                                        'fixed-layout
                                                                        'fluid-layout))})

   'btn                (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'txt                (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'txtarea            (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'dropdown           (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'option})})
   
   'checkbox           (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'checkbox-inline    (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})
   
   'radio              (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'radio-inline       (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   ;; Layout
   'fixed-layout       (partial process-control {:symbol-fn (return `fixed-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available general-layout-tags)})
   
   'fluid-layout       (partial process-control {:symbol-fn (return `fluid-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available general-layout-tags)})
   
   'row                (partial process-control {:symbol-fn (return `row*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-column-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'row
                                                                        'column-))})
   
   'column-            (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-layout-tags
                                                                        'group-for))})

   ;; Navbar
   'navbar             (partial process-control {:symbol-fn (return `navbar*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'brand 'navbar-btn}
                                                            :aliases {'navbar-btn 'btn})
                                                 :transformers [(parse-links-h parse-attrs)
                                                                apply-spacers]})

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
                                                            :aliases {'panel-header 'header})})

   'panel-header       (partial process-control {:symbol-fn (return `panel-header*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-panel-header-tags-with
                                                            :available #{'title})})

   'navpanel           (partial process-control {:symbol-fn (return `navpanel*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'panel-header 'link}
                                                            :aliases {'panel-header 'header})})

   'title              (partial process-control {:symbol-fn tag->qualilified-symbol
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
                                                                        'modal-footer)
                                                            :aliases {'modal-header 'header
                                                                      'modal-footer 'footer})})

   'modal-header       (partial process-control {:symbol-fn (return `modal-header*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-panel-header-tags-with
                                                            :available #{'title})})

   'modal-footer       (partial process-control {:symbol-fn (return `modal-footer*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available general-tags)})

   ;; Forms
   'form               (partial process-form {:symbol-fn tag->qualilified-symbol
                                              :attrs-parser parse-attrs
                                              :expander (expand-tags-with
                                                         :available #{'row 'group-for})})

   'form-horizontal    (partial process-form {:symbol-fn tag->qualilified-symbol
                                              :attrs-parser parse-attrs
                                              :expander (expand-tags-with
                                                         :available #{'row 'group-for})})

   'form-inline        (partial process-form {:symbol-fn tag->qualilified-symbol
                                              :attrs-parser parse-inline-attrs
                                              :expander (expand-tags-with
                                                         :available #{'row 'group-for})
                                              ;; HACK: must render &nbsp after each
                                              ;; form-group to display correctly
                                              :transformers [#(interpose `nbsp* %)]})

   'checkbox-for       (partial process-form-checkbox {:symbol-fn (return `checkbox*)
                                                       :attrs-parser parse-group-for-attrs})

   'group-for          (partial process-control {:symbol-fn (return `form-group*)
                                                 :attrs-parser parse-group-for-attrs
                                                 :expander (expand-group-for-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'form-lbl
                                                                        'form-txt
                                                                        'form-txtarea
                                                                        'form-dropdown
                                                                        'form-checkbox
                                                                        'help
                                                                        'column-
                                                                        'lbl-
                                                                        'txt-
                                                                        'txtarea-
                                                                        'dropdown-
                                                                        'checkbox-
                                                                        'help-)
                                                            :aliases {'form-lbl 'lbl
                                                                      'form-txt 'txt
                                                                      'form-txtarea 'txtarea
                                                                      'form-dropdown 'dropdown
                                                                      'form-checkbox 'checkbox})})

   'form-lbl           (partial process-form-label {:symbol-fn (return `label*)
                                                    :attrs-parser parse-attrs})

   'lbl-               (partial process-form-label {:symbol-fn tag->qualilified-symbol
                                                    :attrs-parser parse-column-attrs})

   'form-txt           (partial process-form-text {:symbol-fn tag->qualilified-symbol
                                                   :attrs-parser parse-attrs})

   'txt-               (partial process-form-text {:symbol-fn tag->qualilified-symbol
                                                   :attrs-parser parse-column-attrs})

   'form-txtarea       (partial process-form-text {:symbol-fn tag->qualilified-symbol
                                                   :attrs-parser parse-attrs})

   'txtarea-           (partial process-form-text {:symbol-fn tag->qualilified-symbol
                                                   :attrs-parser parse-column-attrs})

   'form-dropdown      (partial process-form-dropdown {:symbol-fn tag->qualilified-symbol
                                                       :attrs-parser parse-attrs
                                                       :expander (expand-tags-with
                                                                  :attrs-parser #{'option})})

   'dropdown-          (partial process-form-dropdown {:symbol-fn tag->qualilified-symbol
                                                       :attrs-parser parse-column-attrs
                                                       :expander (expand-tags-with
                                                                  :attrs-parser #{'option})})

   'form-checkbox      (partial process-form-checkbox {:symbol-fn tag->qualilified-symbol
                                                       :attrs-parser parse-attrs})

   'checkbox-          (partial process-form-checkbox {:symbol-fn tag->qualilified-symbol
                                                       :attrs-parser parse-column-attrs})

   'help               (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity})

   'help-              (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander identity})})

(def tags (merge dom-tags com-tags))
