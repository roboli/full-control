full-control
============

A clojurescript Web UI DSL based on Om and Bootstrap.

Take full control of your front end (user interfaces) development writing your code cleaner and faster.

### Installation

Currently only a snapshot is available, include it in your `project.clj` dependencies:

```clojure
[full-control "0.1.0-SNAPSHOT"]

```

### Example

Let's build a home page with a navigation bar and a jumbotron:

```clojure
(ns my-example.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(def app-state (atom {}))

(defpage home [cursor owner]
  (render-state [_]
                (navbar (brand "MyExample")
                        (link {:href "#"} "Home"))
                (fixed-layout
                 (row
                  (column-12
                   (jumbotron
                    (h1 "Welcome!")
                    (p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec ut nisi id turpis interdum cursus.")))))))

(fc/root
 home
 app-state
 {:target (. js/document (getElementById "app"))})
```

### Demo

You can see it working with this little [demo](http://www.roboli.space/full-control) application.

### Contributing

Contributions are welcome! Suggestions/questions use the [issues page](https://github.com/roboli/full-control/issues) or contact me at robertooliveros@mac.com.

### License

Distributed under the Eclipse Public License, the same as Clojure.
