(in-ns 'full-control.core)

;;;
;;; Tags
;;;

(defn return [x]
  (fn [_] x))

(defn- tag->qualilified-symbol [tag]
  `~(symbol (str "full-control.core/" (name tag) "*")))

(def ^:private general-tags #{'with-controls 'p 'button 'h})
(def ^:private general-layout-tags (conj general-tags 'row 'panel 'navpanel))

(def ^:private tags
  {'with-controls      (partial process-with-controls {:expander (expand-tags-with-all)})

   ;; General
   'page               (partial process-control {:symbol-fn (return `page*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-layout-tags
                                                                        'navbar
                                                                        'fixed-layout
                                                                        'fluid-layout))
                                                 :transformers []})

   'p                  (partial process-control {:symbol-fn (return `p*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})
   
   'button             (partial process-control {:symbol-fn (return `button*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})

   'h                  (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})

   ;; Layout
   'fixed-layout       (partial process-control {:symbol-fn (return `fixed-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available general-layout-tags)
                                                 :transformers []})
   
   'fluid-layout       (partial process-control {:symbol-fn (return `fluid-layout*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available general-layout-tags)
                                                 :transformers []})
   
   'row                (partial process-control {:symbol-fn (return `row*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-column-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'row
                                                                        'column-))
                                                 :transformers []})
   
   'column-            (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-column-attrs
                                                 :expander (expand-tags-with
                                                            :available general-layout-tags)
                                                 :transformers []})

   ;; Navbar
   'navbar             (partial process-control {:symbol-fn (return `navbar*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'brand 'button-h}
                                                            :aliases {'button-h 'button})
                                                 :transformers [(parse-links-h parse-attrs)
                                                                apply-spacers]})

   'brand              (partial process-control {:symbol-fn (return `brand*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})
   
   'button-h           (partial process-control {:symbol-fn (return `navbar-button*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})

   ;; Panels
   'panel              (partial process-control {:symbol-fn (return `panel*)
                                                 :attrs-parser parse-layout-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'header
                                                                        'row
                                                                        'stretch))
                                                 :transformers []})

   'header             (partial process-control {:symbol-fn (return `panel-header*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-panel-header-tags-with
                                                            :available #{'title})
                                                 :transformers []})

   'navpanel           (partial process-control {:symbol-fn (return `navpanel*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available #{'header 'link})
                                                 :transformers []})

   'title              (partial process-control {:symbol-fn tag->qualilified-symbol
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})
   
   'stretch            (partial process-control {:symbol-fn (return `stretch*)
                                                 :attrs-parser parse-attrs
                                                 :expander (expand-tags-with
                                                            :available (conj
                                                                        general-tags
                                                                        'row))
                                                 :transformers []})

   'link               (partial process-control {:symbol-fn (return `navpanel-link*)
                                                 :attrs-parser parse-attrs
                                                 :expander identity
                                                 :transformers []})})
