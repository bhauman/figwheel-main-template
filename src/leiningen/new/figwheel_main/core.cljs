(ns ^:figwheel-hooks {{namespace}}
  (:require
   [goog.dom :as gdom]{{#react?}}
   [react :as react]
   [react-dom :as react-dom]
   [sablono.core :as sab :include-macros true]{{/react?}}{{#reagent?}}
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]{{/reagent?}}{{#rum?}}
   [rum.core :as rum]{{/rum?}}))

(println "This text is printed from src/{{main-file-path}}.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))
{{#react?}}
(defn hello-world [state]
  (sab/html [:div
             [:h1 (:text @state)]
             [:h3 "Edit this in src/{{nested-dirs}}.cljs and watch it change!"]]))

(defn mount [el]
  (react-dom/render (hello-world app-state) el))
{{/react?}}{{#reagent?}}
(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/{{nested-dirs}}.cljs and watch it change!"]])

(defn mount [el]
  (rdom/render [hello-world] el))
{{/reagent?}}{{#rum?}}
(rum/defc hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/{{nested-dirs}}.cljs and watch it change!"]])

(defn mount [el]
  (rum/mount (hello-world) el))
{{/rum?}}

{{#framework?}}
(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element){{/framework?}}

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []{{#framework?}}
  (mount-app-element){{/framework?}}
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
