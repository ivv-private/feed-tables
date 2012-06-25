(ns feed-tables.fetch-link
  (:use ;; [clojure.contrib.lazy-xml :only (emit parse-trim)]
   [clojure.xml :only (emit parse)]
        [clojure.contrib.zip-filter.xml :only (xml-> attr)])
  (:require [clojure.zip :as zip])
  (:import java.net.URL java.lang.StringBuilder
           [java.io BufferedReader InputStreamReader]
           [com.google.appengine.api.urlfetch HTTPRequest URLFetchServiceFactory]))

(def content-block #"(?s)(<div class=\"post_body\".*?post_body-->)")
(def charset-block #"(charset=(.*))")

(defn fetch-url-async [address]
  (let [request (HTTPRequest. (URL. address))
        service (URLFetchServiceFactory/getURLFetchService)]
    (. service fetchAsync request)))

(defn process-entry [content entry-loc]
  (let [entry-loc entry-loc
        content (last (re-find content-block content))]
    (zip/append-child entry-loc {:tag :summary, :attrs {:type "html"}, :content (list (str "<![CDATA[ " content "]]>"))})))

(defn xml-apply [loc f]
  (loop [loc loc]
    (if (zip/end? loc)
      (zip/root loc)
      (recur
       (zip/next
        (if (= (:tag (zip/node loc)) :entry)
          (f loc)
          loc))))))

(defn get-charset [headers]
  (let [content-type (first (filter #(. "Content-Type" equalsIgnoreCase (. % getName)) headers))]
    (last (re-find charset-block (. content-type getValue)))))
        
(defn get-page [pages link]
  (let [entry (first (filter #(= (:link %) link) pages))
        response (. (:content entry) get)
        content (. response getContent)
        charset (get-charset (. response getHeaders))]
    (String. content charset)))


(defn process [url]
  (let [loc (-> url parse zip/xml-zip)
        base-url (first (xml-> loc (attr :xml:base)))
        links (xml-> loc :entry :link (attr :href))
        pages (map #(array-map :link % :content (fetch-url-async (str base-url %))) links)]
    (with-out-str
      (emit
       (xml-apply loc
                  #(process-entry
                    (get-page pages (first (xml-> % :link (attr :href))))
                    %))))))
