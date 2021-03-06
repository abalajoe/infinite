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

(def vodka (atom []))
(def whisky (atom []))
(def gin (atom []))
(def brandy (atom []))
(def beer (atom []))
(def beverage (atom []))

(defn list-admin []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows ["select * from tbl_login"]      ; execute query
    (doall rows ))))

;(list-admin)

(defn get-vodka-line-chart []
  "Function display system admin"
  (reset! vodka [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
    (when (> x 0)
     (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Vodka'" x)]      ; execute query
      (doall rows )
      ;(println x "-" (swap! hennessy conj (vals (nth rows 0))))
     ; (println x "-" (swap! vodka conj(nth (vals (nth rows 0)) 0)))
     (swap! vodka conj(nth (vals (nth rows 0)) 0))
      )
      (recur (- x 1))
      )))@vodka)

;(get-vodka-line-chart)

(defn get-whisky-line-chart []
  "Function display system admin"
  (reset! whisky [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
    (when (> x 0)
     (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Whisky'" x)]      ; execute query
      (doall rows )
      ;(println x "-" (swap! whisky conj(nth (vals (nth rows 0)) 0)))
      (swap! whisky conj(nth (vals (nth rows 0)) 0))
      )
       (recur (- x 1))
        )))@whisky)

;(get-whisky-line-chart)

(defn get-brandy-line-chart []
  "Function display system admin"
  (reset! brandy [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
    (when (> x 0)
     (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Brandy'" x)]      ; execute query
      (doall rows )
       ;(println x "-" (swap! brandy conj(nth (vals (nth rows 0)) 0)))
       (swap! brandy conj(nth (vals (nth rows 0)) 0))
       )
       (recur (- x 1))
       )))@brandy)

;(get-brandy-line-chart)

(defn get-gin-line-chart []
  "Function display system admin"
  (reset! gin [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
   (when (> x 0)
    (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Gin'" x)]      ; execute query
     (doall rows )
     ;(println x "-" (swap! gin conj(nth (vals (nth rows 0)) 0)))
     (swap! gin conj(nth (vals (nth rows 0)) 0))
     )
      (recur (- x 1))
      )))@gin)

;(get-gin-line-chart)

(defn get-beer-line-chart []
  "Function display system admin"
  (reset! beer [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
    (when (> x 0)
     (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Beer'" x)]      ; execute query
      (doall rows )
       ;(println x "-" (swap! beer conj(nth (vals (nth rows 0)) 0)))
       (swap! beer conj(nth (vals (nth rows 0)) 0))
       )
        (recur (- x 1))
        )))@beer)

;(get-beer-line-chart)

(defn get-beverage-line-chart []
  "Function display system admin"
  (reset! beverage [])                                      ; reset hennesy to empty vector everytime you call function
  (sql/with-connection db
   (loop [x 7]
    (when (> x 0)
     (sql/with-query-results rows [(format  "select count(*) from tbl_sales join tbl_prod on tbl_sales.product=tbl_prod.name where dayofweek(date_added)=%d and brand='Beverage'" x)]      ; execute query
      (doall rows )
       ;(println x "-" (swap! beverage conj(nth (vals (nth rows 0)) 0)))
       (swap! beverage conj(nth (vals (nth rows 0)) 0))
       )
       (recur (- x 1))
        )))@beverage)

;(get-beverage-line-chart)

(defn list-inventory []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows ["select * from tbl_inventory"]      ; execute query
    (doall rows))))

;(list-inventory)

(defn list-sales []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
    (sql/with-query-results rows ["select * from tbl_sales"]      ; execute query
     (doall rows))))

(defn list-login-logs []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows ["select * from tbl_login_logs"]      ; execute query
    (doall rows ))))

(defn list-audit-logs []
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows ["select * from tbl_audit_logs"]      ; execute query
     (doall rows ))))

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

;(list-admin)
;(:username (nth(list-admin)2))



(defn admin-exists?
  [username]
  "Function display system admin"
  (sql/with-connection db                                   ; open db connection
   (sql/with-query-results rows [(format "select * from tbl_login where username = '%s'" username)]      ; execute query
    (doall rows ))))

(if (not (admin-exists? "abala")) 1 0)

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

(defn insert-login-logs
  "Function inserts login logs to db"
  [username]
  (try
    (let [sql "insert into inventoryapp.tbl_login_logs (username,login_time) values (? , NOW())"]
      (sql/with-connection db
        (sql/do-prepared sql [username])))
    (catch SQLException sql
      (log/error "insert login logs SQLE >> " (.getMessage sql)))
    (catch Exception e
      (log/error "insert login logs E >> " (.getMessage e)))))

;(insert-login-logs "joe")

(defn insert-audit-logs
  "Function inserts audit logs to db"
  [username action description]
  (try
    (let [sql "insert into inventoryapp.tbl_audit_logs (username,action,description,operation_time) values (? , ? , ? ,  NOW())"]
      (sql/with-connection db
        (sql/do-prepared sql [username action description])))
    (catch SQLException sql
      (log/error "insert audit logs SQLE >> " (.getMessage sql)))
    (catch Exception e
      (log/error "insert audit logs E >> " (.getMessage e)))))

;(insert-audit-logs "joe" "add admin" "successfull")

(defn insert-inventory [liquor brand size quantity price]
  "Function inserts inventory to db"
  (let [sql "insert into inventoryapp.tbl_inventory (liquor,brand,size,quantity,price,date) values (?, ?, ?, ?, ?, NOW())"]
    (sql/with-connection db
     (sql/do-prepared sql [liquor brand size quantity price] ))))

(defn insert-sales [liquor size]
  "Function inserts inventory to db"
  (let [sql "insert into inventoryapp.tbl_sales (liquor,size,date_added) values (?, ?, now())"]
    (sql/with-connection db
     (sql/do-prepared sql [liquor size]))))

;(insert "test" "test")

#_(let [sql "insert into inventoryapp.login (username,password) values (? , ?)"]
  (sql/with-connection db
    (sql/do-prepared sql ["abala" "abala"] )))

(defn login-test [^String username ^String password ^String type]
  (sql/with-connection db
     (sql/with-query-results rs ["select * from tbl_login_test where username=? and password=? and type=?" username password type]
       (dorun (map #(println %) rs))
         (count rs))))

;(login-test "user" "user" "user")

(defn login-test-pdf []
  (sql/with-connection db
   (sql/with-query-results rs ["select * from tbl_login_test"]
    (doall rs))))

;(login-test-pdf)

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
(def user-type-atom (atom []))

;; get liqours
(defn get-liqours []
  (reset! liquor-atom [])
  (sql/with-connection db
   (sql/with-query-results rs ["select name from tbl_prod"]
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

;(get-liqours)

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

(defn get-user-type []
  (reset! user-type-atom [])
  (sql/with-connection db
   (sql/with-query-results rs ["select type from tbl_login_test"]
    (loop [x (count rs)]
    (when (not (= x 0))
    ; (println  "main -> " (:name(nth rs (- x 1))))
    (swap! user-type-atom conj (:type(nth rs (- x 1))))
     (recur (dec x))))
     (count rs)
       @user-type-atom )))

;(get-user-type)


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







