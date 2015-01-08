;; namespace & libs
;; Namespace contains helper functions
(ns util.utils
  (:import (javax.swing JOptionPane JTable SwingUtilities JFrame JPanel JScrollPane JDialog)
           (javax.swing.table AbstractTableModel)
           (java.awt GridLayout Dimension)
           (java.awt.event MouseAdapter)
           (org.jfree.chart ChartPanel ChartFactory)
           (org.jfree.chart.plot PlotOrientation)
           (org.jfree.data.general DefaultPieDataset))
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojure.tools.logging :as log]))

;; configuration-file path
(def conf "E:\\infinite\\infinite\\src\\config.properties")

;;; Vars to control some default plotting behaviors ;;;
(def ^:dynamic *legend* true)
(def ^:dynamic *tooltips* true)
(def ^:dynamic *urls* true)
(def ^:dynamic *orientation* PlotOrientation/VERTICAL)

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

(defn- pie-dataset
  "Internal function to convert from a sequence of pairs into a pie chart dataset"
  ([data]
    (let [pds (new DefaultPieDataset)]
      (doseq [i data] (.setValue pds (first i) (second i)))
      pds)))

(defn pie
  "Create a single pie chart from a sequence of category-value pairs.
   Each category-value pair should itself be a sequence.
   e.g. => (pie \"This is the Title\" '((\"Emacs\" 20) (\"Vi\" 15) (\"Eclipse\" 30)))"
  ([title pairs]
    (ChartFactory/createPieChart
      title (pie-dataset pairs) *legend* *tooltips* *urls*)))

(defn dialog-box
  "Function displays dialog box"
  [data-ref title product]
  (let [dialog (new JDialog (@data-ref :owner) true)]
    (doto dialog
      (.add (new ChartPanel
                 (pie title product)))
      (.setSize 500 500)
      (.setLocationRelativeTo nil)
      (.setVisible true))
    (@data-ref :username)))

(defn dialog-string [owner title product]
  (dialog-box (ref {:owner owner}) title product))

;; table model
(defn model [rows col-names value-at]
  (proxy [AbstractTableModel] []
    (getRowCount []    (count rows))
    (getColumnCount [] (count col-names))
    (getColumnName [c]
      (clojure.string/upper-case(.substring (nth col-names c)1)))            ;remove colon from column name using substring and capitalize column names
    (getValueAt [r c]  (value-at r c))
    (isCellEditable [r c] false)))

(defn double-click? [event]
  (= 2 (.getClickCount event)))

(defn add-mouse-listener
  [component]
  (let [listener (proxy [MouseAdapter] []
                   (mouseClicked [event]
                     (and (double-click? event) (SwingUtilities/isLeftMouseButton event)
                          (println "working..."))))]
    (.addMouseListener component listener)
    listener))


(defn display-table [data title table]
  "This table shows all the administrators
  for the system and does not allow editing"
  (println "display table")
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
                       (.setFillsViewportHeight true)
                       )))))
         (.setSize 1000 300)
         (.setLocationRelativeTo nil)
         (.setVisible true)))))

;; table instance for editing

(def edit-admin-table (JTable.))
(def test-table (doto (JTable.)))
(def edit-inventory-table (JTable.))
(def edit-sales-table (JTable.))