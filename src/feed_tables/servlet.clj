(ns feed-tables.servlet
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use compojure.core ring.util.servlet)
  (:require [compojure.route :as route]
            [feed-tables.fetch-link :as fetch-link]
            [feed-tables.cache :as cache]
            [feed-tables.storage :as storage])
  (:import [com.google.appengine.api.taskqueue QueueFactory TaskOptions$Builder]))

(defroutes main-routes

  (GET "/fetch-link" [] (cache/obtain "http://feed.rutracker.org/atom/f/249.atom"))
  (GET "/fetch-link-n" [] (storage/get-feed "feed.rutracker.org-atom-f-249-atom"))
  (GET "/fetch-link-d" [] (storage/get-feed "feed.rutracker.org-atom-f-249-atom"))

  (GET "/feeds/sync" []
       (let [queue (QueueFactory/getDefaultQueue)
             opts (TaskOptions$Builder/withUrl "/feeds/fetch-link")]
         (. queue add opts)
         "submitted"))
                       
  (POST "/feeds/fetch-link" [url]
        (do
          (storage/put "feed.rutracker.org-atom-f-249-atom" (fetch-link/process "http://feed.rutracker.org/atom/f/249.atom"))))
  
  (route/not-found "404 D'oh"))


(defservice main-routes)
