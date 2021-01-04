(ns leiningen.new.figwheel-main
  (:require [leiningen.new.templates :refer [renderer project-name
                                             ->files sanitize-ns name-to-path
                                             multi-segment]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def render (renderer "figwheel-main"))

(def supported-frameworks ["reagent" "rum" "react"])

(def framework-opts (set (map #(str "--" %) supported-frameworks)))

(def supported-attributes #{"lein" "bare-index" "deps" "npm-bundle"})

(def attribute-opts (set (map #(str "+" %) supported-attributes)))

;; I copy this levenshtein impl everywhere
(defn- next-row
  [previous current other-seq]
  (reduce
    (fn [row [diagonal above other]]
      (let [update-val (if (= other current)
                          diagonal
                          (inc (min diagonal above (peek row))))]
        (conj row update-val)))
    [(inc (first previous))]
    (map vector previous (next previous) other-seq)))

(defn- levenshtein
  [sequence1 sequence2]
  (peek
    (reduce (fn [previous current] (next-row previous current sequence2))
            (map #(identity %2) (cons nil sequence2) (range))
            sequence1)))

(defn- similar [ky ky2]
  (let [dist (levenshtein (str ky) (str ky2))]
    (when (<= dist 2) dist)))

(defn similar-options [opt]
  (second (first (sort-by first
                  (filter first (map (juxt (partial similar opt) identity)
                                     (concat framework-opts attribute-opts)))))))

(defn parse-opts [opts]
  (reduce (fn [accum opt]
            (cond 
              (framework-opts opt) (assoc accum :framework (keyword (subs opt 2)))
              (attribute-opts opt) (update accum :attributes
                                           (fnil conj #{})
                                           (keyword (subs opt 1)))
              :else
              (let [suggestion (similar-options opt)]
                (throw
                 (ex-info (format "Unknown option '%s' %s"
                                  opt
                                  (str
                                   (when suggestion
                                     (format "\n    --> Perhaps you intended to use the '%s' option?" suggestion))))
                          {:opts opts
                           ::error true})))))
          {} opts))

(defn in-clj? []
  (resolve 'clj-new.helpers/create))

(defn test-runner-ns [main-ns]
  (string/join "." [(first (string/split main-ns #"\.")) "test-runner"]))

(defn windows? []
  (.contains (string/lower-case (System/getProperty "os.name")) "windows"))

(defn opts-data [n {:keys [framework attributes]}]
  (let [to-att #(keyword (str (name %) "?"))
        main-ns (multi-segment (sanitize-ns n))
        test-run-ns (test-runner-ns main-ns)
        inclj (in-clj?)
        data (cond-> {:raw-name n
                      :name (project-name n)
                      :namespace main-ns
                      :test-runner-ns test-run-ns
                      :test-runner-dirs (name-to-path test-run-ns)
                      :lein? (not inclj)
                      :deps? (boolean inclj)
                      :windows? (windows?)
                      :main-file-path (-> (name-to-path main-ns)
                                          (string/replace "\\" "/"))
                      :nested-dirs (name-to-path main-ns)}
               framework
               (->
                (assoc (to-att :framework) true)
                (assoc (to-att framework) true))
               (not-empty attributes) (#(reduce
                                         (fn [accum att]
                                           (assoc accum (to-att att) true))
                                         % attributes)))]
    (cond-> data
      (and (or (:react? data)
               (:reagent? data))
           (:npm-bundle? data))
      (assoc :reactdep? true))))

(defn figwheel-main
  "Takes a name and possibly a single framework option with the form
  --framework and any number of attribute options of the form
  +attribute and produces a minimal ClojureScript project that
  includes Figwheel Main tooling

  The framework options are:
     --react   which adds a minimal React/Sablono application in core.cljs
     --reagent which adds a minimal Reagent application in core.cljs
     --rum     which adds a minimal Rum application in core.cljs
     --om      which adds a minimal Om application in core.cljs

  The attribute options are:
     +deps        which generates a deps.edn (a default when used with clj-new)
     +lein        which generates a project.clj (a default when used with lein)
     +bare-index  which generates an index without any annoyingly helpful content

  Only one **framework** option can be` specified at a time. If no
  framework option is specified, nothing but a print statment is added
  to the generated ClojureScript code."
  [name & opts]
  (do
    (when (#{"figwheel" "cljs"} name)
      (main/abort
       (format
        (str "Cannot name a figwheel project %s the namespace will clash.\n"
             "Please choose a different name, maybe \"tryfig\"?")
        (pr-str name))))
    (try
      (let [parsed-opts (parse-opts opts)
            data (opts-data name parsed-opts)
            base-files [["README.md" (render "README.md" data)]
                        ["figwheel-main.edn" (render "figwheel-main.edn" data)]
                        ["dev.cljs.edn" (render "dev.cljs.edn" data)]
                        ["test.cljs.edn" (render "test.cljs.edn" data)]
                        ["src/{{nested-dirs}}.cljs" (render "core.cljs" data)]
                        ["resources/public/css/style.css" (render "style.css" data)]
                        ["resources/public/test.html" (render "test-page.html" data)]
                        [".gitignore" (render "gitignore" data)]
                        ["test/{{nested-dirs}}_test.cljs" (render "test.cljs" data)]
                        ["test/{{test-runner-dirs}}.cljs" (render "test-runner.cljs" data)]
                        ["test/{{test-runner-dirs}}.cljs" (render "test-runner.cljs" data)]
                        ]
            files (cond-> base-files
                    (:lein? data)
                    (conj ["project.clj" (render "project.clj" data)])
                    (:deps? data)
                    (conj ["deps.edn" (render "deps.edn" data)])
                    (:npm-bundle? data)
                    (conj ["package.json" (render "package.json" data)])
                    (and (not (:bare-index? data))
                         (not (:framework? data)))
                    (conj ["resources/public/index.html" (render "index.html" data)])
                    (or (:bare-index? data)
                        (:framework? data))
                    (conj ["resources/public/index.html" (render "bare-index.html" data)]))]
        (main/info (format (str "Generating fresh figwheel-main project.\n"
                                "  To get started:\n"
                                "  -->  Change into the '%s' directory\n"
                                (if (:npm-bundle? data)
                                  "  -->  IMPORTANT: run 'npm install'\n")
                                "  -->  Start build with '%s'\n")
                           (:name data)
                           (if (:deps? data)
                             "clojure -M:fig:build"
                             "lein fig:build")))
        (apply ->files data files)
        ;; ensure target directory
        (.mkdirs (io/file (:name data) "target" "public")))
      (catch clojure.lang.ExceptionInfo t
        (if (-> t ex-data ::error)
          (main/warn (.getMessage t))
          (throw t))))))
