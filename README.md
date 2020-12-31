# figwheel.main template

A template generator that will produce a minimal ClojureScript project
that includes
[figwheel.main](https://figwheel.org)
tooling.

## Abridged Usage Examples

Already an expert? Can't stand to read more information? Assuming you have [lein](https://github.com/technomancy/leiningen) or [clj-new](https://github.com/seancorfield/clj-new) installed, you can use one of the following commands:

```shell
clj -X:new create :template figwheel-main :name yourname/hello-world :args '["+npm-bundle","--reagent"]' # or --rum, --react or nothing
```

or

```shell
lein new figwheel-main hello-world.core -- +npm-bundle --reagent # or --rum, --react or nothing
```

## Overview

The `figwheel-main` template is intended to get you up and running
with a no-frills ClojureScript project initialized with the
ClojureScript React framework of your choosing. It is intended to work
equally well with
[Leiningen](https://github.com/technomancy/leiningen) or
[clj-new](https://github.com/seancorfield/clj-new).

## Usage

#### Using clj-new

Make sure you have
[installed the Clojure CLI tools](https://clojure.org/guides/getting_started#_clojure_installer_and_cli_tools)
and are running the latest version.

Also, ensure you have installed [clj-new](https://github.com/seancorfield/clj-new) as detailed [here](https://github.com/seancorfield/clj-new#getting-started) 
	
	clj -X:new create :template figwheel-main :name yourname/hello-world :args '["--reagent"]'

#### Using lein

Make sure you have the
[latest version of Leiningen installed](https://github.com/technomancy/leiningen#installation).

    lein new figwheel-main hello-world.core -- --reagent

### Options

Takes a **name** and possibly a single **framework** option with the
form `--framework` and any number of **attribute** options of the form
`+attribute` and produces a minimal ClojureScript project that
includes Figwheel Main tooling

The initial **name** option is intended to be the name of your initial
project namespace. Here are some examples of valid project names:

	hello.core
	my.group/dominate.world

The framework options are:

     --react   which adds a minimal React/Sablono application in core.cljs
     --reagent which adds a minimal Reagent application in core.cljs
     --rum     which adds a minimal Rum application in core.cljs
     --om      which adds a minimal Om application in core.cljs

The attribute options are:

     +npm-bundle  which sets up a build that utilizes npm modules
     +deps        which generates a deps.edn (a default when used with clj-new)
     +lein        which generates a project.clj (a default when used with lein)
     +bare-index  which generates an index without any annoyingly helpful content

Only one **framework** option can be specified at a time. If no
framework option is specified, nothing but a print statment is added
to the generated ClojureScript code.

Examples:

	lein new figwheel-main hello-world.core -- +deps +npm-bundle --react 

    clj -X:new create :template figwheel-main :name yourname/hello-world :args '["+bare-index","+lein","--react"]'

# Compiling the generated project

> When using `+npm-bundle` you must run `npm install` before building your project

### With a CLI tools project

To get an interactive development environment change into the project
root (the directory just created) and execute:

    clojure -A:fig:build
	
After the compilation process is complete, and a browser has popped
open the compiled project in your browser, you will get a ClojureScript
REPL prompt that is connected to the browser.

An easy way to verify this is:

    cljs.user=> (js/alert "Am I connected?")

and you should see an alert in the browser window.

You can also supply arguments to `figwheel.main` like so:

	clojure -A:fig -b dev -r

To clean all compiled files:

    rm -rf target/public

To create a production build:

	rm -rf target/public
    clojure -A:fig:min
	
### With Leiningen based project

To get an interactive development environment change into the project
root (the directory just created) and execute:

    lein fig:build
	
After the compilation process is complete, and a browser has popped
open the compiled project in your browser, you will get a ClojureScript
REPL prompt that is connected to the browser.	

An easy way to verify this is:

    cljs.user=> (js/alert "Am I connected?")

and you should see an alert in the browser window.

You can also supply arguments to `figwheel.main` like so:

	lein fig -- -b dev -r

To clean all compiled files:

	lein clean

To create a production build:

	lein clean
    lein fig:min

# Testing

Your initial tests are in the `test` directory. You will have tests
that are updated live with each file change if you open a tab to
`http://localhost:9500/figwheel-extra-main/auto-testing`

You can run your tests from the command line with:

    clj -A:fig:test
	
or with `lein`:

    lein fig:test

## Questions?

See the [`figwheel.main` documentation](https://figwheel.org)

Hit me up on the [Clojurians Slack](http://clojurians.net/) on the `#figwheel-main` channel. 

## License

Copyright © 2018 Bruce Hauman

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
