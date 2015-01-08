(ns layout.analysis
  (:import
    (org.jfree.chart ChartFactory ChartFrame ChartPanel)
    (org.jfree.chart.plot PlotOrientation)
    (org.jfree.data.general DefaultPieDataset)
    (javax.swing JTextArea JDialog JLabel)
    (java.awt GridBagLayout Insets GridLayout))
  (:require [model.db :as db]
            [util.utils :as utl]))

;;; Vars to control some default plotting behaviors ;;;
(def ^:dynamic *legend* true)
(def ^:dynamic *tooltips* true)
(def ^:dynamic *urls* true)
(def ^:dynamic *orientation* PlotOrientation/VERTICAL)

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

(pie "hey" (db/liquor-brand))

(defn liquor-piechart
  "Function displays inventory pie chart for liquors"
  []
  (doto (new ChartPanel
             (pie "LIQUOR" (db/liquor-names)))))

(liquor-piechart)

(defn brand-piechart
  "Function displays inventory pie chart for liquor brands"
  []
  (doto (new ChartFrame "Inventory Pie Chart"
             (pie "LIQUOR BRANDS" (db/liquor-brand)))
    (.pack)
    (.setVisible true)
    (.setLocationRelativeTo nil)))

(defn size-piechart
  "Function displays inventory pie chart for liquor brands"
  []
  (doto (new ChartFrame "Inventory Pie Chart"
             (pie "LIQUO SIZES " (db/liquor-size)))
    (.pack)
    (.setVisible true)
    (.setLocationRelativeTo nil)))

(defn admin-dialog [data-ref title product]
  (let [dialog (new JDialog (@data-ref :owner) true)]
    (doto dialog
      (.add (new ChartPanel
                 (pie title product)))
      (.setSize 600 600)
      (.setLocationRelativeTo nil)
      (.setVisible true))
    (@data-ref :username)))

(defn dialog-string [owner title product]
  (admin-dialog (ref {:owner owner}) title product))

;(dialog-string nil "liqour" (db/liquor-size))