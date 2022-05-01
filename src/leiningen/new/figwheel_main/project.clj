(defproject {{ raw-name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.11.4"]{{#react?}}
                 {{^npm-bundle?}}[cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]{{/npm-bundle?}}
                 [sablono "0.8.6" :exclusions [com.cognitect/transit-clj com.cognitect/transit-java]]{{/react?}}{{#reagent?}}
                 {{^npm-bundle?}}[cljsjs/react "17.0.2-0"]
                 [cljsjs/react-dom "17.0.2-0"]{{/npm-bundle?}}
                 [reagent "1.1.1" {{#npm-bundle?}} :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]{{/npm-bundle?}}]{{/reagent?}}{{#rum?}}
                 [rum "0.12.9"]{{/rum?}}]

  :source-paths ["src"]

  :aliases {"fig:build" [{{^windows?}}"trampoline" {{/windows?}}"run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]{{^rum?}}
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "{{test-runner-ns}}"]{{/rum?}}}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.17"]
                                  [org.slf4j/slf4j-nop "1.7.30"]{{^windows?}}
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]{{/windows?}}]
                   {{#deps?}}:clean-targets ^{:protect false} [:target-path "resources/public/cljs-out"]{{/deps?}}
                   {{^deps?}}:resource-paths ["target"]
                   ;; need to add the compiled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target"]{{/deps?}}}})

