# figwheel-template

A Leinigen template to get started with [Figwheel](https://github.com/bhauman/lein-figwheel/figwheel-main).

## Usage

Make sure you have ether [leiningen](https://github.com/technomancy/leiningen). or [clj-new](https://github.com/seancorfield/clj-new) installed

#### Using lein

Make sure you have the [latest version of leiningen installed](https://github.com/technomancy/leiningen#installation).

    lein new figwheel-main hello-world -- --reagent +lein
	
#### Using clj-new

Make sure you have installed `clj-new` as detailed [here](https://github.com/seancorfield/clj-new#getting-started) 
	
	clj -A:new figwheel-main hello-world -- --reagent

### Options

Takes a **name** and possibly a single **framework** option with the form
`--framework` and any number of **attribute** options of the form `+attribute`
and produces a ClojureScript + Figwheel Main template.

The framework options are:

     --react   which adds a minimal React/Sablono application in core.cljs
     --reagent which adds a minimal Reagent application in core.cljs
     --rum     which adds a minimal Rum application in core.cljs
     --om      which adds a minimal Om application in core.cljs

The attribute options are:

     +lein        which generates a project.clj
     +bare-index  which generates an index without any annoyingly helpful content

Only one **framework** option can be specified at a time. If no
framework option is specified, nothing but a print statment is added
in core.clj

Include the options using `--` to separate them from Leiningens
options, like so

    clj -A:new figwheel-main hello-world -- --react

## Usage with CLI tools

To get an interactive development environment run 

    clojure -Acljs:build
	
from the project root directory.

Figwheel will now be running and will auto compile and send all
changes to the browser without the need to reload.

After the compilation process is complete, and you have loaded the
compiled project in your browser you will get a ClojureScript REPL
prompt that is connected to the browser.

An easy way to verify this is:

    cljs.user=> (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    rm -rf target/public

To create a production build run:

	rm -rf target/public
    clojure -Acljs:min

## License

Copyright © 2018 Bruce Hauman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
