(defproject {{ raw-name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.773"]{{#react?}}
                 {{^npm-bundle?}}[cljsjs/react "16.4.1-0"]
                 [cljsjs/react-dom "16.4.1-0"]{{/npm-bundle?}}
                 [sablono "0.8.6"]{{/react?}}{{#reagent?}}
                 [reagent "0.10.0" {{#npm-bundle?}} :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]{{/npm-bundle?}}]{{/reagent?}}{{#rum?}}
                 [rum "0.12.3"]{{/rum?}}]

  :source-paths ["src"]

  :aliases {"fig"       [{{^windows?}}"trampoline" {{/windows?}}"run" "-m" "figwheel.main"]
            "fig:build" [{{^windows?}}"trampoline" {{/windows?}}"run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "{{test-runner-ns}}"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.14"]{{^windows?}}
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]{{/windows?}}]
                   {{#deps?}}:clean-targets ^{:protect false} [:target-path "resources/public/cljs-out"]{{/deps?}}
                   {{^deps?}}:resource-paths ["target"]
                   ;; need to add the compiled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target"]{{/deps?}}}})

