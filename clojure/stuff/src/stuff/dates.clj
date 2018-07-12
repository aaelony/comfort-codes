(ns stuff.dates
  (:require [clojure.string :as str]
            [clj-time
             [core :as time]
             [format :as time-f]
             [coerce :as time-c]]))

(defn now-epoch
  "Return epoch as a Long"
  []
  (-> (time/now)
      time-c/to-long))

(defn today
  "Return a string in format of yyyy-mm-dd"
  []
  (subs (.toString (time/now)) 0 10))
;; (today)


(defn epoch-to-timestamp
  [epoch]
  (let [clean-f (fn [s]
                  (-> s
                      (str/replace #"-" "")
                      (str/replace #":" "")
                      (str/replace #"T" "")
                      (subs 0 12)))]
    (->> epoch
         time-c/from-long
         (time-f/unparse (time-f/formatters :date-time))
         clean-f
         )))






