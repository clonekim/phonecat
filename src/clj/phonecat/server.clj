(ns phonecat.server
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [defroutes GET POST DELETE ANY ]]
            [compojure.route :refer [resources not-found]]
            [compojure.handler :refer [site]]
            [compojure.coercions :refer [as-int]]
            [clojure.java.io :as io]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :as response]))


(def -phone-list
  [{:id 1 :name "Samsung Galaxy S 2" :img "sgs2.png"}
   {:id 2 :name "Nexus 4" :img "nexus4.png"}
   {:id 3 :name "Xperia TL" :img "xperia-tl.png"}
   {:id 4 :name "Samsung Galaxy Nexus" :img "galxy-nexus.png"}
   {:id 5  :name "Kyocera Milano" :img "kyocera-milano.png"}])


(defn index-page []
  {:status 200
   :header {"Context-Type" "text/html"}
   :body   (-> "public/index.html"
               io/resource
               slurp)})


(defn phone-list []
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body -phone-list})

(defn phone-detail [id]
  (println "finding phone -->" id)
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (->> -phone-list
              (filter #(= id (:id %)))
              first)})


(defroutes routes
  (GET "/" [] (index-page))
  (GET "/_/phone" [] (phone-list))
  (GET "/_/phone/:id" [id :<< as-int] (phone-detail id))
  (resources "/")
  (not-found "<p>Page not found</p>"))

(def app
  (-> routes
      (wrap-json-body {:keywords? true :bigdecimals? true})
      wrap-json-response))

(defn -main []
  (println "Server Started...")
  (run-server app {:port 3000}))
