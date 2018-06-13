(defproject {{ raw-name }} "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]{{#react?}}
                 [cljsjs/react "15.6.1-1"]
                 [cljsjs/react-dom "15.6.1-1"]
                 [sablono "0.8.3"]{{/react?}}{{#om?}}
                 [cljsjs/react "15.6.1-1"]
                 [cljsjs/react-dom "15.6.1-1"]
                 [sablono "0.8.3"]
                 [org.omcljs/om "1.0.0-alpha46"]{{/om?}}{{#reagent?}}
                 [reagent "0.7.0"]{{/reagent?}}{{#rum?}}
                 [rum "0.11.2"]{{/rum?}}]

  :source-paths ["src"]

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.1.1"]
                                  [com.bhauman/rebel-readline-cljs "0.1.3"]]
                   :resource-paths ["resources" "target"]
                   ;; need to add the compliled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["target/public" :target-path]}})

