full-control
============

A clojurescript Web UI DSL based on [Om](https://github.com/omcljs/om) and [Bootstrap](https://github.com/twbs/bootstrap).

Take full control of your front end development writing your code cleaner and faster.

### Installation

Currently only a snapshot is available, include it in your `project.clj` dependencies:

```clojure
[full-control "0.1.0-SNAPSHOT"]

```
Please read Om's [build configuration](https://github.com/omcljs/om#build-configuration) for further instructions.

### Requirements

Depends heavily on Bootstrap (obsviously) and JQuery, and it has been tested with versions 3.3.0 and 1.11.0 respectively. Don't forget to include the links in the head section of your html document, like this:

```html
<html>
  <head>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/bootstrap/3.3.0/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/bootstrap/3.3.0/css/bootstrap-theme.min.css">

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://cdn.jsdelivr.net/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </head>
  ...
```

And for the Datepicker and Autocomplete controls you need the links for the jquery scripts and styles:

```html
...
    <link rel="stylesheet" href="../path/to/jquery-ui.css">
    <link rel="stylesheet" href="../path/to/jquery-ui.theme.css">
    <script src="../path/to/jquery.js"></script>
    <script src="../path/to/jquery-ui.js"></script>
...
```
You can build this jquery-ui.* files [here](http://jqueryui.com/download/) and make sure they have the necessary dependecies in order for these two controls to work properly. 

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
                    (p "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")))))))

(fc/root
 home
 app-state
 {:target (. js/document (getElementById "app"))})
```

And we get this:

![](https://github.com/roboli/full-control/blob/master/examples/my-example/images/jumbotron.png)

### Demo

You can see it working with this little [demo](http://www.roboli.space/full-control) application.

### Wiki

Under construction [here](https://github.com/roboli/full-control/wiki).

### Contributing

Contributions are welcome! Suggestions/questions use the [issues page](https://github.com/roboli/full-control/issues) or contact me at robertooliveros@mac.com.

### License

Distributed under the Eclipse Public License, the same as Clojure.
