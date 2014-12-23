;; namespace & libs
;; Namespace contains database operations
(ns model.db
  (:import (java.sql SQLException))
  (:require [clojure.java.jdbc :as sql])
  (:require [clojure.tools.logging :as log])
  (:require [clojure.java.io :as io])
  (:require [util.utils :as utl]))

(def db
  "Database configurations"
  (let [classname (utl/get-configuration :classname)
        subprotocol (utl/get-configuration :subprotocol)
        subname (utl/get-configuration :subname)
        user (utl/get-configuration :user)
        password (utl/get-configuration :password)]
    {:classname classname
     :subprotocol subprotocol
     :subname subname
     :user user
     :password password}))


(defn list-admin []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows ["select * from tbl_login"]      ; execute query
    (log/info rows )rows)))

(defn delete-admin
  "Function deletes an admin"
  [id]
  (log/infof "[delete-admin] function exec" )
  (try
    (let [sql "delete from tbl_login where id = ?"]
      (sql/with-connection db
                           (sql/do-prepared sql [id])))
    (catch SQLException e
      (log/errorf "SQLException delete-admin [%s]" e))
    (catch Exception e
      (log/errorf "Exception delete-admin [%s]" e))))

(defn update-admin
  "Function updates administrator"
  [password id]
  (log/infof "[delete-admin] function exec" )
  (try
    (let [sql "update tbl_login set password = ? where id = ?"]
      (sql/with-connection db
                           (sql/do-prepared sql [password id])))
    (catch SQLException e
      (log/errorf "SQLException update-admin [%s]" e))
    (catch Exception e
      (log/errorf "Exception update-admin [%s]" e))))

;(list-admin)
;(:username (nth(list-admin)2))

(defn list-inventory []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
                       (sql/with-query-results rows ["select * from tbl_add_inventory"]      ; execute query
                                               (log/info rows )rows)))

(defn liquor-names []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
                       (sql/with-query-results rows ["select liquor,quantity from tbl_add_inventory"]      ; execute query
                                               (for [i (doall rows)] (list (:liquor i) (:quantity i))))))
;(for [i (doall rows)] (str i))

;(liquor-names)

(defn liquor-brand []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
                       (sql/with-query-results rows ["select brand,count(*) as count from tbl_add_inventory group by brand"]      ; execute query
                                               (for [i (doall rows)] (list (:brand i) (:count i))))))
;
;(liquor-brand)

(defn liquor-size []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
                       (sql/with-query-results rows ["select size,count(*) as count from tbl_add_inventory group by size"]      ; execute query
                                               (for [i (doall rows)] (list (:size i) (:count i))))))

;(liquor-size)

(defn list-user []
  (sql/with-connection db
                       (sql/with-query-results rows ["select * from login where id=1"]
                                               (println rows )
                                               (count rows))))

(defn insert-admin
  "Function inserts admin to db"
  [username password]
  (try
    (let [sql "insert into inventoryapp.tbl_login (username,password,date_added) values (? , ? , NOW())"]
      (sql/with-connection db
        (sql/do-prepared sql [username password])))
    (catch SQLException sql
      (log/error "insert admin SQLE >> " (.getMessage sql)))
    (catch Exception e
      (log/error "insert admin E >> " (.getMessage e)))))

;(insert-admin "joe" "jwizzy")

(defn insert-inventory [liquor brand size quantity price]
  "Function inserts inventory to db"
  (let [sql "insert into inventoryapp.tbl_inventory (liquor,brand,size,quantity,price,date) values (?, ?, ?, ?, ?, NOW())"]
    (sql/with-connection db
                         (sql/do-prepared sql [liquor brand size quantity price] ))))

(defn insert-sales [liquor size]
  "Function inserts inventory to db"
  (let [sql "insert into inventoryapp.tbl_sales (liquor,size) values (?, ?)"]
    (sql/with-connection db
                         (sql/do-prepared sql [liquor size]))))

;(insert "test" "test")

#_(let [sql "insert into inventoryapp.login (username,password) values (? , ?)"]
  (sql/with-connection db
    (sql/do-prepared sql ["abala" "abala"] )))

(defn login [^String username ^String password]
  (sql/with-connection db
                       (sql/with-query-results rs ["select * from login where username=? and password=?" username password]
                                               (dorun (map #(println %) rs))
                                               (count rs))))

;(login "abala" "abalat")

(defn check-admin [^String username]
  "Function checks if specified admin exists"
  (sql/with-connection db                                   ;; connect to db
                       (sql/with-query-results rs ["select * from login where username=?" username ] ;; execute query
                                               (dorun (map #(println %) rs))
                                               (count rs))))

;(check-admin "admin")

;; atom
(def liquor-atom (atom []))
(def brand-atom (atom []))
(def size-atom (atom []))

;; get liqours
(defn get-liqours []
  (reset! liquor-atom [])
  (sql/with-connection db
                       (sql/with-query-results rs ["select name from tbl_liquor"]
                                               #_(dorun
                                                #_ (map #(println %)rs))
                                               ;(println (:name(second rs)))
                                               (loop [x (count rs)]
                                                 (when (not (= x 0))
                                                   ; (println  "main -> " (:name(nth rs (- x 1))))
                                                   (swap! liquor-atom conj (:name(nth rs (- x 1))))
                                                   (recur (dec x))))
                                               (count rs)
                                               @liquor-atom )))

;; get brands
(defn get-brands []
  (reset! brand-atom [])
  (sql/with-connection db
                       (sql/with-query-results rs ["select name from tbl_brands"]
                                               #_(dorun
                                               #_ (map #(println %)rs))
                                               ;(println (:name(second rs)))
                                               (loop [x (count rs)]
                                                 (when (not (= x 0))
                                                   ; (println  "main -> " (:name(nth rs (- x 1))))
                                                   (swap! brand-atom conj (:name(nth rs (- x 1))))
                                                   (recur (dec x))))
                                               (count rs)
                                               @brand-atom )))

;; get size
(defn get-size []
  (reset! size-atom [])
  (sql/with-connection db
                       (sql/with-query-results rs ["select size from tbl_size"]
                                               #_(dorun
                                                #_ (map #(println %)rs))
                                               ;(println (:name(second rs)))
                                               (loop [x (count rs)]
                                                 (when (not (= x 0))
                                                   ; (println  "main -> " (:name(nth rs (- x 1))))
                                                   (swap! size-atom conj (:size(nth rs (- x 1))))
                                                   (recur (dec x))))
                                               (count rs)
                                               @size-atom )))
;(get-brands)



;; update loans
(defn update-loans [amount msisdn]
  "Function updates db"
  (let [sql "update cdrparser.recepient set amount = ? where msisdn =? "]
    (sql/with-connection db
                         (sql/do-prepared sql [amount msisdn]))))

;(update-loans 250 721123456)

;; parse cdr
#_(with-open [rdr (io/reader "E:\\cdrtest.txt")]
  (doseq [line (line-seq rdr)]
    (let [data (clojure.string/split line #",")
          msisdn (get data 2)
          amount (get data 3)]
      (println msisdn)
      (println amount)
      (println "........")
      (update-loans amount msisdn)
      (println "updated " msisdn " with " amount))
    (println "--------")))



;(create-users)
;(list-users)







