(ns layout.reports
  (:require  [model.db :as db]                               ; db
             [clojure.tools.logging :as log]                 ; log
             [clj-pdf.core :as pdf]))                        ; generate pdf

(def admin-report-data  (into [] (db/login-test-pdf)))

(def admin-template
  (pdf/template [(.toString $id) $username $password $type]))

(def inventory-report-data  (into [] (db/list-inventory)))

(def inventory-template
  (pdf/template [(.toString $id) $liquor $brand $size (.toString $quantity) (.toString $price) $date]))

(def sales-report-data  (into [] (db/list-sales)))

(def sales-template
  (pdf/template [(.toString $id) $product $size $date_added]))

(defn generate-pdf
  [title template data report & headers]
  (try
    (pdf/pdf
      [{:header title}
       (into [:table
              {:border true
               :cell-border false
               :header [{:color [0 150 150]} headers]}]
             (template data))]
      report)
    (catch Exception e
      (log/error (.getMessage e)))))

;(generate-pdf "emp list" admin-template admin-report-data "report.pdf" "ID" "User" "Password" "Type")
