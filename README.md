full-control [Deprecated]
============

A clojurescript Web UI DSL based on [Om](https://github.com/omcljs/om) and [Bootstrap](https://github.com/twbs/bootstrap).

Take full control of your front end development writing code cleaner and faster.

## Installation

Currently only a snapshot is available, include it in your `project.clj` dependencies:

```clojure
[full-control "0.1.0-SNAPSHOT"]

```
Please read Om's [build configuration](https://github.com/omcljs/om#build-configuration) for further instructions.

## Requirements

* Bootstrap (obviously). Tested with version 3.3.0.

* JQuery UI for the Datepicker and Autocomplete controls. Tested with version 1.11.

## Example

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
                    (p "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")))))))

(fc/root
 home
 app-state
 {:target (. js/document (getElementById "app"))})
```

And we get this:

![](https://github.com/roboli/full-control/blob/master/examples/my-example/images/jumbotron.png)

## Demo

You can see it working with this little [demo](http://www.roboli.space/full-control) application.

## Wiki

Under construction [here](https://github.com/roboli/full-control/wiki).

## Contributing

Contributions are welcome! Suggestions/questions use the [issues page](https://github.com/roboli/full-control/issues) or contact me at robertooliveros@mac.com.

## License

Distributed under the Eclipse Public License, the same as Clojure.
