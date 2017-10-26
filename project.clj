(defproject phonecat "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [org.clojure/core.async  "0.3.443"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [reagent "0.7.0"]
                 [secretary "1.2.3"]
                 [cljs-ajax "0.7.2"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot phonecap.server
  :clean-targets ^{:protect false} [:target-path [:cljsbuild :builds :app :compiler :output-dir] 
                                    [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel {:http-server-root "public"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}


  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.7"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [figwheel-sidecar "0.5.14"]]

                   :plugins [[lein-figwheel "0.5.14"]
                             [org.clojure/clojurescript "1.9.946"]]

                   :cljsbuild
                   {:builds
                    {:app
                     {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                      :figwheel     {:on-jsload "phonecat.core/mount-components"}
                      :compiler     {:main phonecat.app
                                     :asset-path "/js/out"
                                     :output-to "target/cljsbuild/public/js/phonecat.js"
                                     :output-dir "target/cljsbuild/public/js/out"
                                     :source-map true
                                     :optimizations :none
                                     :pretty-print true}}}}}})
