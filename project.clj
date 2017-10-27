(defproject phonecat "0.2"
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
                 [metosin/muuntaja "0.3.2"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [reagent "0.7.0"]
                 [secretary "1.2.3"]
                 [clj-time "0.14.0"]
                 [cljs-ajax "0.7.2"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot phonecat.server
  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :app :compiler :output-dir] 
                                    [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel {:http-server-root "public"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  

  :profiles
  {:uberjar {:omit-source true
             :uberjar-name "phonecat.jar"
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :aot :all
             :source-paths ["env/prod/clj"]
             :cljsbuild
             {:builds
              {:min
               {:source-paths  ["src/cljs" "src/cljc" "env/prod/cljs"]
                :compiler      {:main phonecat.app
                                :asset-path "js/release"
                                :output-to "target/cljsbuild/public/js/phonecat.js"
                                :output-dir "target/cljsbuild/public/js/release"
                                :optimizations :advanced
                                :pretty-print false}}}}}


   :dev {:dependencies [[binaryage/devtools "0.9.7"]
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
