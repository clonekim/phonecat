(ns ^:figwheel-no-load phonecat.app
  (:require [phonecat.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)
(devtools/install!)
(core/init!)
