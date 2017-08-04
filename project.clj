(defproject see-queue-are-ess "0.0.1-SNAPSHOT"
  :description "A CQRS library for Clojure"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [trptcolin/versioneer "0.2.0"]]

  :min-lein-version "2.0.0"
  :pedantic? :abort

  :profiles {:dev
             {:aliases {"test" "midje"
                        "quality" ["with-profile" "quality" "test"]}
              :plugins [[lein-midje "3.2.1"
                         :exclusions [org.clojure/tools.namespace]]]
              :dependencies [[org.clojure/tools.namespace "0.2.11"]
                             [midje "1.8.3"]]}
             :quality [:dev
                       {:injections [(require 'midje.config)
                                     (midje.config/change-defaults
                                       :check-after-creation
                                       false)]
                        :aliases {"test" ^:replace ["do"
                                                    ["bikeshed"]
                                                    ["eastwood"]
                                                    ["kibit"]
                                                    ["kibit" "test"]]}
                        :plugins [[jonase/eastwood "0.2.4"]
                                  [lein-bikeshed "0.4.1"
                                   :exclusions [org.clojure/tools.namespace
                                                org.clojure/tools.cli]]
                                  [lein-kibit "0.1.5"
                                   :exclusions [org.clojure/clojure]]]}]})
