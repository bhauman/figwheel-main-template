(defproject {{ raw-name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.12.0"]
                 [org.clojure/clojurescript "1.11.132"]
                 [org.clojure/data.json "2.5.1"]{{#react?}}
                 {{^npm-bundle?}}[cljsjs/react "18.0.0-rc.0-0"]
                 [cljsjs/react-dom "18.0.0-rc.0-0"]{{/npm-bundle?}}
                 [sablono "0.8.6" :exclusions [com.cognitect/transit-clj com.cognitect/transit-java]]{{/react?}}{{#reagent?}}
                 {{^npm-bundle?}}[cljsjs/react "18.0.0-rc.0-0"]
                 [cljsjs/react-dom "18.0.0-rc.0-0"]{{/npm-bundle?}}
                 [reagent "1.2.0" {{#npm-bundle?}} :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server]{{/npm-bundle?}}]{{/reagent?}}{{#rum?}}
                 [rum "0.12.11"]{{/rum?}}]

  :source-paths ["src"]

  :aliases {"fig:build" [{{^windows?}}"trampoline" {{/windows?}}"run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:clean" ["run" "-m" "figwheel.main" "--clean" "dev"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]{{^rum?}}
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" "{{test-runner-ns}}"]{{/rum?}}}

  :profiles {:dev {:dependencies [[org.slf4j/slf4j-nop "2.0.3"]
                                  [com.bhauman/figwheel-main "0.2.20"]{{^windows?}}
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]{{/windows?}}]
                   :resource-paths ["target"]
                   {{#deps?}}:clean-targets ^{:protect false} [:target-path "resources/public/cljs-out"]{{/deps?}}
                   {{^deps?}};; need to add the compiled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target"]{{/deps?}}}})

