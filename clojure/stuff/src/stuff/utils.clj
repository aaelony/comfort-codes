(ns stuff.core
  (:require [clojure.java.io :refer [reader writer copy file] :as io]            
            [clojure.string :as str]
            [clj-time [core :as time] [coerce :as c] [periodic :as time-period]]
            [net.cgrand.xforms :as x]
            [clojure.pprint :refer [pprint print-table]]
            [me.raynes.fs :as fs]
            [clojure.java.shell :as sh]
            [taoensso.timbre :refer [info warn error] :as timbre]
   ))

(defn dcast
  "dcast like in R. Source: cgrand."
  [coll keys xform]
  (into []
        (x/by-key #(select-keys % keys) ; keyfn
                  #(reduce dissoc % keys) ; not sure it's useful, identity may be enough
                  into ; pair
                  (comp xform
                        (x/into {}))) ;xform
        coll))


(defn read-data
  "In: Filename, delimiter.
   Out: Sequence of clojure maps, using the first line of the file
       the header for the names of the keys."
 [filename sep]
 (with-open [r (io/reader filename)]
   (let [[headers & lines] (->> r line-seq (map #(str/split % (re-pattern sep))))]
     (doall (map #(zipmap (map keyword headers) %)
                 lines)))))


(defn delimited-gz-file
  "Output a gzip compressed, delimited file."
  [delimiter out-filename records & field-order]
  (let [field-keys  (if (vector? (ffirst field-order))
                      (ffirst field-order)  
                      (-> records first keys sort vec))
        field-labels  (mapv #(str/replace (name %) "-" "_")
                            field-keys)
        ]
    (timbre/info (str "Using field-order of " field-keys " for out-file '" out-filename "' "))
    ;;(with-open [wrt (writer out-filename)]
    (with-open [wrt (-> out-filename
                        clojure.java.io/output-stream
                        java.util.zip.GZIPOutputStream. 
                        clojure.java.io/writer)]
      ;; header
      (.write wrt
              (apply str
                     (apply str
                            (interpose delimiter field-labels)) "\n"))
      ;; data
      (doseq [row records]
        (doall (for [key field-keys
                     :let [datum (key row)
                           clean-datum (clojure.string/escape (str datum) {\" "'"})
                           ]
                     ]
                    (.write wrt
                            (str (if (= delimiter ",")
                                   (str \" clean-datum \" )
                                   (key row))
                                 (if-not (= key (last field-keys)) delimiter)  ;; no delimiter after the last field.
                                 ))))
        (.write wrt "\n")))
    (.exists (io/file out-filename))))

(defn tsv-gz-file
  "Output a gzip'd, tab-delimited file."
  [out-filename records & field-order]
  (delimited-gz-file "\t" out-filename records field-order))

(defn csv-gz-file
  "Output a gzip'd, tab-delimited file."
  [out-filename records & field-order]
  (if field-order
    (delimited-gz-file "," out-filename records field-order)
    (delimited-gz-file "," out-filename records)
    ))


(comment
 (defn view-as-html [incanter-dataset]
  "Construct Hiccup style HTML markup instructions.
   Input an incanter dataset, outputs markup 
   instructions for an HTML table.
  "
  [:table {:border 0}
   [:thead
    [:tr
     (for [field-name (i/col-names incanter-dataset)]
       [:th field-name])]]
   [:tbody
    (for [r (second (second incanter-dataset))]
      [:tr  (for [k (i/col-names incanter-dataset) ]
              [:td (str (k r))]
              )])]])


 (defn run-query-file [db-spec sql-file]
 "Run an SQL query against a database using JDBC,
  and return a complete result set from that query 
  as an Incanter Dataset.  
  
  Arguments:
     db-spec      - a map with keys [:user :password :subname :subprotocol ]
                    e.g.

                   (def mysql-db {:subprotocol \"mysql\"
                                  :subname \"//127.0.0.1:3306/clojure_test\"
                                  :user \"clojure_test\"
                                  :password \"clojure_test\"})

     sql-file     - a file containing SQL  
  "
  (clojure.java.jdbc/with-connection db-spec
    (clojure.java.jdbc/with-query-results data
      (-> (slurp sql-file)
          (clojure.string/replace #"\n" " ")
          (clojure.string/replace #" {1,}" " ")
          (vector))
      (incanter.core/to-dataset (doall data)))))
;; (def data (run-query-file dw/dw "long-query.sql"))
;; (incanter.core/dim data6)

 )


