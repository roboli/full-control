full-control
============

A clojurescript Web UI DSL based on Om and Bootstrap.

Proof of Concept. This repository contains a working prototype.

You can see it working with this little [demo](http://www.roboli.space/full-control) application.

### Motivation

#### The problem

Front-end development is hard. It's even harder if you don't have the right tools. Of course, if what you're working on is no more than a form, some inputs and a button, you can rely on whatever you see fit. But this is not always the case, and when you're programming a big or complex application, one must defer to third party libraries to accelerate the process. Third party libraries where almost all of them, not only they not free but also expensive. But why is front-end development so hard? Part of the problem relies on its technologies. Take HTML for example, what was designed to describe documents is now use to describe controls/widgets. There are so many libraries, tools, toolkits and frameworks to use, where each one try their best to encapsulate all those complexities and try their best to create a layer of abstraction, but most often than not, you end up distracted, you end up investing more time writing code that has nothing to do with the logic of your application.

#### The solution, or can it be?

Remember the old days of desktop development? Sure, they were ugly as hell interfaces but weren't they straightforward? A textbox control there, another over there, a button right here, do your bindings and end of story (kind of). RAD was accomplished because you were not distracted. Web technologies are making strides to get over this point, and every year were getting closer and closer. But what if we could push it a little bit farther? And this were I believe Clojurescript has an edge over the rest. Being a lisp and taking full advantage of its macro system (as you may know), what if we can build a language out of it, an DSL to describe Web UI interfaces. Please read the following incomplete code to see what I'm trying to tell you:

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

If you don't understand what this code is doing, then I'll return to my cave and work harder. Otherwise I say, mission accomplished!

### Contributing

Now, the main goal of this repository was to understand and overcome the problem at hand. It was developed to see how a DSL would look like in order to be useful. And it works, but it has limitations and it is not for production. So, if it happens that you share this same ideas and you believe that it would be a great contribution to our community, please contact me. Lets build it together!

TODO: Explain examples.
