(ns phonecat.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [ajax.core :refer [GET]])
  (:import goog.History))



(defonce state (atom {}))

(defn render-page [components & coll]
  (->> (. js/document
          (getElementById "app"))
       (reagent/render-component (if coll [components coll] [components]))))


(defn render-navbar []
  (reagent/render-component [:nav.navbar.navbar-default
                             [:div.container-fluid
                              [:div.navbar-header
                               [:a.navbar-brand "PhoneCat"]]
                              [:ul.nav.navbar-nav
                               [:li [:a {:href "/#/"} "Home"]]
                               [:li [:a {:href "/#/phone"} "Phones"]]]]]
                            (. js/document (getElementById "nav"))))


(defn home-page []
  [:div.container-fluid
   [:div.row
    [:h3 "Welcome to PhoneCat!"]]])


(defn phone-item [{:keys [id name img]}]
  [:div.col-md-3.col-xs-6
   [:div.thumbnail
    [:img {:src (str "/img/" img)}]
    [:div.caption
     [:h3 name]
     [:p.text-right
      [:a.btn.btn-default.btn-lg
       {:href (str "/#/phone/" id)}
       [:span.glyphicon.glyphicon-zoom-in] "Detail"]]]]])


(defn phone-list []
  [:div.row
   (if-let [phones (:phones @state)]
     (for [{:keys [id] :as phone} phones]
       ^{:key id} [phone-item phone])
     [:h3 "No phones found"])])


(defn phone-detail []
  (when-let [{:keys [id name img]} (:phone @state)]
    [:div
     [:ol.breadcrumb
      [:li [:a {:href "/#/"}       "Home"]]
      [:li [:a {:href "/#/phone"} "Phones"]]
      [:li.active id]]
     [:div.panel
      [:img {:src (str "/img/" img)}]
      [:p name]]]))


;;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (render-page home-page))

(secretary/defroute "/phone" []
  (GET "/_/phone"
      :format :json
      :keywords? true
      :handler (fn [res]
                 (swap! state assoc :phones res)))
  (render-page phone-list))


(secretary/defroute "/phone/:id" [id]
  (GET (str "/_/phone/" id)
      :handler (fn [res]
                 (swap! state assoc :phone res)))
  (render-page phone-detail))


(secretary/defroute "*" []
  (render-page (fn []
                 [:p "404 Not Found"])))

;;; History
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen 
     HistoryEventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))


(defn mount-components []
  (render-navbar)
  (render-page home-page))

(defn init! []
  (hook-browser-navigation!)
  (mount-components))
