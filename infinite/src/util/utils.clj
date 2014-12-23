;; namespace & libs
;; Namespace contains helper functions
(ns util.utils
  (:import (javax.swing JOptionPane JTable)
           (javax.swing.table AbstractTableModel))
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojure.tools.logging :as log]))

;; configuration-file path
(def conf "E:\\infinite\\infinite\\src\\config.properties")

(defn- load-config
  "Function loads configuration file"
  [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(defn get-configuration
  "Function gets configuration"
  [key]
  ((load-config conf)key))

(defn convert-to-long
  "Function converts string to Long"
  [value]
  (try
    (Long/parseLong value)
    (catch NumberFormatException e
      (log/error "convert-to-long NFE => " (.getMessage e))
      (JOptionPane/showMessageDialog
        nil (str "Only numbers allowed, \"" value "\" is not a valid number!") "Input Problem"
        JOptionPane/ERROR_MESSAGE))
    (catch Exception e
      (log/error "convert-to-long E => " (.getMessage e))
      (JOptionPane/showMessageDialog
        nil "Problem with input!" "Input Problem"
        JOptionPane/ERROR_MESSAGE))))

;; macro for setting gridbaglayout constraints
(defmacro set-grid! [constraints field value]
  `(set! (. ~constraints ~(symbol (name field)))
         ~(if (keyword? value)
            `(. java.awt.GridBagConstraints
                ~(symbol (name value)))
            value)))

;; macro to add components to a container
(defmacro grid-bag-layout [container & body]
  (let [c (gensym "c")
        cntr (gensym "cntr")]
    `(let [~c (new java.awt.GridBagConstraints)
           ~cntr ~container]
       ~@(loop [result '() body body]
           (if (empty? body)
             (reverse result)
             (let [expr (first body)]
               (if (keyword? expr)
                 (recur (cons `(set-grid! ~c ~expr
                                          ~(second body))
                              result)
                        (next (next body)))
                 (recur (cons `(.add ~cntr ~expr ~c)
                              result)
                        (next body)))))))))

;; table instance used for display
(def show-admin-table (JTable.))

;; table instance for editing
(def edit-admin-table (JTable.))

;; table model
(defn model [rows col-names value-at]
  (proxy [AbstractTableModel] []
    (getRowCount []    (count rows))
    (getColumnCount [] (count col-names))
    (getColumnName [c]
      (clojure.string/upper-case(.substring (nth col-names c)1)))            ;remove colon from column name using substring and capitalize column names
    (getValueAt [r c]  (value-at r c))
    (isCellEditable [r c] false)))