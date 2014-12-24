;; namespace & libs
;; Namespace contains helper functions
(ns util.utils
  (:import (javax.swing JOptionPane JTable SwingUtilities JFrame JPanel JScrollPane)
           (javax.swing.table AbstractTableModel)
           (java.awt GridLayout Dimension))
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

;; table model
(defn model [rows col-names value-at]
  (proxy [AbstractTableModel] []
    (getRowCount []    (count rows))
    (getColumnCount [] (count col-names))
    (getColumnName [c]
      (clojure.string/upper-case(.substring (nth col-names c)1)))            ;remove colon from column name using substring and capitalize column names
    (getValueAt [r c]  (value-at r c))
    (isCellEditable [r c] false)))

(defn display-table [data title table]
  "This table shows all the administrators
  for the system and does not allow editing"
  (def rows data)
  (. SwingUtilities invokeLater
     (fn []
       (doto (JFrame. title)
         (.setDefaultCloseOperation (. JFrame HIDE_ON_CLOSE))
         (.setContentPane
           (doto (JPanel. (GridLayout. 1 0))
             (.setOpaque true)
             (.add (JScrollPane.
                     (doto table
                       (.setModel (model rows
                                             (vec (map str (keys (first rows))))
                                             (fn [r c] ((nth rows r) (nth (keys (first rows)) c)))))
                       (.setPreferredScrollableViewportSize
                         (Dimension. 800 300))
                       (.setFillsViewportHeight true))))))
         (.setSize 1000 300)
         (.setLocationRelativeTo nil)
         (.setVisible true)))))

;; table instance for editing
(def edit-admin-table (JTable.))
