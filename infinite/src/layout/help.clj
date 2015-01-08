(ns layout.help
  (:import (javax.swing JDialog  JTextArea)
           (java.awt GridBagLayout Insets))
  (:require [clojure.tools.logging :as log]
            [util.utils :as utl]))

(def about-app "This is an Inventory management software solutions
that keeps track of inventory, sales and purchasing")

;; jdialog
(defn help-dialog [data-ref]
  ; cleate dialog with text area
  (let [dialog (new JDialog (@data-ref :owner) true)
        about-label (doto (new JTextArea about-app 0 0)
                   (.setEditable false)
                   (.setLineWrap false)
                   (.setWrapStyleWord true))]
    (doto dialog
      ; set dialog layout
      (.setLayout (new GridBagLayout))
      ; style the components
      (utl/grid-bag-layout
        :fill :BOTH, :insets (Insets. 5 1 1 5)
        :gridx 0, :gridy 0, :anchor :LINE_START
        about-label)
      (.setSize 500 200)
      (.setLocationRelativeTo nil)
      (.setVisible true))
    (@data-ref :username)))

(defn dialog-string [owner]
  (help-dialog (ref {:owner owner})))
(dialog-string nil)
