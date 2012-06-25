(defproject feed-tables "0.1-SNAPSHOT"
  :description "Feed tables"
  :dependencies  [[org.clojure/clojure "1.3.0"]
                  [org.clojure/clojure-contrib "1.2.0"]
                  [ring/ring-servlet "0.3.11"]
                  [compojure "0.6.4"]
                  [com.google.appengine/appengine-api-1.0-sdk "1.6.0"]
                  [com.google.appengine/appengine-jsr107cache "1.6.0"]
                  ;; [enlive "1.0.0"]
                  ]
 
  :dev-dependencies [[uk.org.alienscience/leiningen-war "0.0.13"]
                     [javax.servlet/servlet-api "2.5"]
                     [swank-clojure "1.3.3"]]

  :aot [feed-tables.servlet]
  :war {:name "feed-tables.war"}

  :omit-default-repositories true
  :repl-port 4005
)
