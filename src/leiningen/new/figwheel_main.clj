(ns leiningen.new.figwheel-main
  (:require [leiningen.new.templates :refer [renderer project-name
                                             ->files sanitize-ns name-to-path
                                             multi-segment]]
            [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(def render (renderer "figwheel-main"))

(def supported-frameworks ["om" "reagent" "rum" "react"])

(def framework-opts (set (map #(str "--" %) supported-frameworks)))

(def supported-attributes #{"lein" "bare-index"})

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

(defn opts-data [n {:keys [framework attributes]}]
  (let [to-att #(keyword (str (name %) "?"))
        main-ns (multi-segment (sanitize-ns n))]
    (cond-> {:raw-name n
             :name (project-name n)
             :namespace main-ns
             :nested-dirs (name-to-path main-ns)}
      framework (assoc (to-att framework) true)
      (not-empty attributes) (#(reduce
                                (fn [accum att]
                                  (assoc accum (to-att att) true))
                                % attributes)))))

(defn figwheel-main
  "Takes a name and possibly a single framework option with the form
  --framework and any number of attribute options of the form +attribute
  and produces a ClojureScript + Figwheel Main template.

  The framework options are:
     --react   which adds a minimal React/Sablono application in core.cljs
     --reagent which adds a minimal Reagent application in core.cljs
     --rum     which adds a minimal Rum application in core.cljs
     --om      which adds a minimal Om application in core.cljs

  The attribute options are:
     +lein        which generates a project.clj
     +bare-index  which generates an index without any helpful content

  Only one option framework option can be specified at a time. If no
  framework option is specified, nothing but a print statment is added
  in core.cljs"
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
            _ (clojure.pprint/pprint data)
            base-files [["README.md" (render "README.md" data)]
                        ["deps.edn" (render "deps.edn" data)]
                        ["dev.cljs.edn" (render "dev.cljs.edn" data)]
                        ["src/{{nested-dirs}}.cljs" (render "core.cljs" data)]
                        ["resources/public/css/style.css" (render "style.css" data)]
                        [".gitignore" (render "gitignore" data)]]
            files (cond-> base-files
                    (:lein? data)
                    (conj ["project.clj" (render "project.clj" data)])
                    (not (:bare-index? data))
                    (conj ["resources/public/index.html" (render "index.html" data)])
                    (:bare-index? data)
                    (conj ["resources/public/index.html" (render "bare-index.html" data)]))]
        (main/info (format (str "Generating fresh figwheel-main project.\n"
                                "   -->  To get started: Change into the '%s' directory and run '%s'\n")
                           (:name data)
                           (if (:lein? data)
                             "lein fig:build"
                             "clojure -Afig:build")))
        (apply ->files data files)
        ;; ensure target directory
        (.mkdirs (io/file (:name data) "target" "public")))
      (catch clojure.lang.ExceptionInfo t
        (if (-> t ex-data ::error)
          (main/warn (.getMessage t))
          (throw t))))))
