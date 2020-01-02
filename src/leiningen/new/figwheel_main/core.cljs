(ns ^:figwheel-hooks {{namespace}}
  (:require
   [goog.dom :as gdom]{{#react?}}
   [react :as react]
   [react-dom :as react-dom]
   [sablono.core :as sab :include-macros true]{{/react?}}{{#om?}}
   [react :as react]
   [react-dom :as react-dom]
   [create-react-class :as create-react-class]
   [sablono.core :as sab :include-macros true]
   [om.core :as om :include-macros true]{{/om?}}{{#reagent?}}
   [reagent.core :as reagent :refer [atom]]{{/reagent?}}{{#rum?}}
   [rum.core :as rum]{{/rum?}}))

(println "This text is printed from src/{{main-file-path}}.cljs. Go ahead and edit it and see reloading in action.")

(defn multiply [a b] (* a b))
{{#om?}}
;; this is to support om with the latest version of React
(set! (.-createClass react) create-react-class){{/om?}}

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn get-app-element []
  (gdom/getElement "app"))
{{#react?}}
(defn hello-world [state]
  (sab/html [:div
             [:h1 (:text @state)]
             [:h3 "Edit this in src/{{main-file-path}}.cljs and watch it change!"]]))

(defn mount [el]
  (js/ReactDOM.render (hello-world app-state) el))
{{/react?}}{{#om?}}
(defn mount [el]
  (om/root
   (fn [data owner]
     (reify om/IRender
       (render [_]
         (sab/html
          [:div
           [:h1 (:text data)]
           [:h3 "Edit this in src/{{main-file-path}}.cljs and watch it change!"]]))))
   app-state
   {:target el}))
{{/om?}}{{#reagent?}}
(defn hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/{{main-file-path}}.cljs and watch it change!"]])

(defn mount [el]
  (reagent/render-component [hello-world] el))
{{/reagent?}}{{#rum?}}
(rum/defc hello-world []
  [:div
   [:h1 (:text @app-state)]
   [:h3 "Edit this in src/{{main-file-path}}.cljs and watch it change!"]])

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
