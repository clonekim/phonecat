(ns phonecat.server
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET POST DELETE ANY ]]
            [compojure.route :refer [resources not-found]]
            [compojure.handler :refer [site]]
            [compojure.coercions :refer [as-int]]
            [clojure.java.io :as io]
            [ring.util.response :as response]
            [cheshire.generate :as cheshire]
            [cognitect.transit :as transit]
            [muuntaja.core :as muuntaja]
            [muuntaja.format.json :refer [json-format]]
            [muuntaja.format.transit :as transit-format]
            [muuntaja.middleware :refer [wrap-format wrap-params]])

  (:import [org.joda.time ReadableInstant])
  (:gen-class))


(def -phone-list
  [{:id 1 :name "Samsung Galaxy S 2" :img "sgs2.png"}
   {:id 2 :name "Nexus 4" :img "nexus4.png"}
   {:id 3 :name "Xperia TL" :img "xperia-tl.png"}
   {:id 4 :name "Samsung Galaxy Nexus" :img "galxy-nexus.png"}
   {:id 5  :name "Kyocera Milano" :img "kyocera-milano.png"}])


(defn index-page []
  {:status 200
   :headers {"Context-Type" "text/html"}
   :body   (-> "public/index.html"
               io/resource
               slurp)})


(defn phone-list []
  {:status 200
   :body -phone-list})

(defn phone-detail [id]
  (println "Get Phone details --" id)
  {:status 200
   :body (->> -phone-list
              (filter #(= id (:id %)))
              first)})


(defroutes routes
  (GET "/" [] (index-page))
  (GET "/_/phone" [] (phone-list))
  (GET "/_/phone/:id" [id :<< as-int] (phone-detail id))
  (resources "/")
  (not-found "<p>Page not found</p>"))


(def joda-time-writer
  (transit/write-handler
    (constantly "m")
    (fn [v] (-> ^ReadableInstant v .getMillis))
    (fn [v] (-> ^ReadableInstant v .getMillis .toString))))

(cheshire/add-encoder
  org.joda.time.DateTime
  (fn [c jsonGenerator]
    (.writeString jsonGenerator (-> ^ReadableInstant c .getMillis .toString))))

(def restful-format-options
  (update
    muuntaja/default-options
    :formats
    merge
    {"application/json"
     json-format

     "application/transit+json"
     {:decoder [(partial transit-format/make-transit-decoder :json)]
      :encoder [#(transit-format/make-transit-encoder
                   :json
                   (merge
                     %
                     {:handlers {org.joda.time.DateTime joda-time-writer}}))]}}))

(defn wrap-formats [handler]
  (let [wrapped (-> handler wrap-params (wrap-format restful-format-options))]
    (fn [request]
      ((if (:websocket? request) handler wrapped) request))))


(def app
  (-> routes
      wrap-formats))

(defn -main []
  (println "Server Started...")
  (run-server app {:port 3000}))
