full-control
============

A clojurescript Web UI DSL based on Om and Bootstrap.

Proof of Concept. This repository contains a working prototype.

You can see it working with this little [demo](http://www.roboli.space/full-control) application.

### Motivation

#### The problem

Frontend development is hard. It's even harder if you don't have the rigth tools. Of course, if what you're working on it's no more than a form, some inputs and a button, you can rely on whatever you see fit. But its not always the case, and when you're programming a big or complex application, one must recur to third party libraries to acelerate the process. Third party libraries where almost all of them, not only they aren't free but they are expensive. But why frontend development is so hard? Part of the problem relies on its technologies. Take HTML for example, what was designed to describe documents it is now use to describe controls/widgets. And there are so many libraries, tools, toolkits and frameworks to use, where each one try their best to encapsulate all those complexities and try their best to create a layer of abstraction, but most often than not, you end up distracted, you end up investing more time writing code that has nothing to do with the logic of your application.

#### The solution, or can it be?

Remember the old days of desktop development? Sure, they were hell of ugly interfaces but weren't they straightforward? A textbox control there, another over there, a button right here, do your bindings and end of story (kind of). RAD was accomplished because you were not distracted. Web technologies are making strides to get over this point, and every year were getting closer and closer. But what if we could push it a little bit farther? And this were I believe Clojurescript has an edge over the rest. Being a lisp and taking full advantage of its macro system (as you may know), what if we can build a language out of it, an DSL to describe Web UI interfaces. Please read the following incomplete code to see what I'm trying to tell you:

```clojure
...

(def app-state (atom {:item {:description "Screw Driver"
                             :price 44.5}}))

...

    (panel
      (header (title3 "My Form"))
      (frm
        (with-record (:item cursor)
          (row
            (column-6
              (group-for :description
                         (lbl)
                         (txt {:max-length 50})
            (column-6
              (group-for :price
                         (lbl)
                         (txt {:max-length 15})
          (row
            (column-12
              (btn {:on-click #(do-your-thing-fn (:item @cursor))} "Go!"))))))

...
```

If you have don't understand what this code is doing, then I'll return to my cave and work harder. Otherwise I say mission accomplished!

TODO: Explain examples.
