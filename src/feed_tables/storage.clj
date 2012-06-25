(ns feed-tables.storage
  (:import [com.google.appengine.api.files FileServiceFactory AppEngineFile AppEngineFile$FileSystem]
           [com.google.appengine.api.blobstore BlobstoreServiceFactory BlobInfoFactory]
           [java.nio ByteBuffer]
           [java.lang StringBuilder]))

(def blob_fetch_chunk 1015808)

(defn put [name content]
  (let [service (FileServiceFactory/getFileService)
        file (. service createNewBlobFile "text/xml" name)
        content content]
    (doto (. service openWriteChannel file true)
      (.write (-> (. content getBytes "utf-8") (ByteBuffer/wrap)))
      (.closeFinally))
    (. file getFullPath)))

(defn find-blob-info [name]
  (let [infos (iterator-seq (. (BlobInfoFactory.) queryBlobInfos))]
    (first (filter #(= (. % getFilename) name) infos))))

(defn chunked-seq
  ([chunk-size blob-len]
     (if (> chunk-size blob-len)
       (list [0 (- blob-len 1)])
       (chunked-seq chunk-size blob-len [0 (- chunk-size 1)])))
  ([chunk-size blob-len range]
     (let [[start-index end-index] range
           next-start-index (min blob-len (+ end-index 1))
           next-end-index (min blob-len (+ next-start-index (- chunk-size 1)))]
       (if (= next-end-index blob-len)
         (lazy-seq (cons range (cons [next-start-index next-end-index] ())))
         (lazy-seq (cons range (chunked-seq chunk-size blob-len [next-start-index next-end-index])))))))

(defn get-feed [name]
  (let [blob-service (BlobstoreServiceFactory/getBlobstoreService)
        file-service (FileServiceFactory/getFileService)
        blob-info (find-blob-info name)
        file (AppEngineFile. (AppEngineFile$FileSystem/BLOBSTORE) (.. blob-info getBlobKey getKeyString))
        blob-key (. file-service getBlobKey file)
        blob-size (. blob-info getSize)
        buf (StringBuilder. blob-size)]
    (doseq
        [[start end] (chunked-seq blob_fetch_chunk blob-size)]
      (. buf append (String. (. blob-service fetchData blob-key start end) "utf-8")))
    (. buf toString)))


(defn get-feed [name]
  (let [blob-service (BlobstoreServiceFactory/getBlobstoreService)
        file-service (FileServiceFactory/getFileService)
        blob-info (find-blob-info name)
        file (AppEngineFile. (AppEngineFile$FileSystem/BLOBSTORE) (.. blob-info getBlobKey getKeyString))
        blob-key (. file-service getBlobKey file)
        blob-size (. blob-info getSize)
        buf (StringBuilder. blob-size)]
    (doseq
        [[start end] (chunked-seq blob_fetch_chunk blob-size)]
      (. buf append (String. (. blob-service fetchData blob-key start end) "utf-8")))
    (. buf toString)))
    
    
    ;; (String. (. blob-service fetchData blob-key 0 (. blob-info getSize)))))
    
