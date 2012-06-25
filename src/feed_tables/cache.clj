(ns feed-tables.cache
  (:import [com.google.appengine.api.memcache MemcacheServiceFactory]))

(defn obtain [key]
  (. (MemcacheServiceFactory/getMemcacheService "feed-tables.feeds") get key))

(defn populate [key f]
  (let [cache (MemcacheServiceFactory/getMemcacheService "feed-tables.feeds")
        value (f)]
    (do
      (. cache put key value)
      value)
    ))