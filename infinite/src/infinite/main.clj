
(ns infinite.main
  (:import (javax.swing JFrame JMenu JMenuBar JMenuItem)
           (java.awt.event ActionListener))
  (:require [clojure.tools.logging :as log]
            [layout.admin :as admin]))

;; main frame
(def main-frame)

;; menubar
(def menuBar
  (doto (JMenuBar.)
    (.add
      (doto (JMenu. "File")
            (.add
              (doto
                (JMenuItem. "Admin")))
            (.add
              (doto
                (JMenuItem. "Inventory")))
            (.add (JMenuItem. "Suppliers"))
            (.add (JMenuItem. "Expenditure"))
            (.add (JMenuItem. "Budget"))
            (.add (JMenuItem. "Graphs"))
            (.add (JMenuItem. "Reports"))))
    (.add
      (doto (JMenu. "Edit")
            (.add
              (doto (JMenu. "Admin")
                (.add (doto (JMenuItem. "Add Admin")
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Add Admin")
                              (admin/exec-admin-frame)
                              )))))
                (.add (doto (JMenuItem. "Edit Admin")
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Edit Admin")
                              (admin/edit-admin-table)
                              )))))))
            (.add
              (doto
                (new JMenu "Inventory")
                (.add
                  (doto
                    (new JMenuItem "Add Inventory")
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Add Inventory")
                          ;(pnl/inventory-frame)
                          )))))
                (.add
                  (doto
                    (new JMenuItem "Display Inventory")
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Display Inventory")
                          ;(tbl/inventory-table)
                          )))))
                (.add
                  (doto
                    (new JMenu "Inventory Analysis")
                    (.add
                      (doto
                        (new JMenuItem "Liquor")
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (log/info "Liquor analysis")
                              ;(chart/liquor-piechart)
                              )))))
                    (.add
                      (doto
                        (new JMenuItem "Brand")
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (log/info "Brand analysis")
                              ; (chart/brand-piechart)
                              )))))
                    (.add
                      (doto
                        (new JMenuItem "Size")
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (log/info "Size analysis")
                              ; (chart/size-piechart)
                              )))))))))
            (.add
              (doto
                (new JMenu "Sales")
                (.add
                  (doto
                    (new JMenuItem "Record Sales")
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Sales")
                          ; (pnl/sales-frame)
                          )))
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Sales")
                          ;(pnl/sales-frame)
                          )))))
                (.add
                  (doto
                    (new JMenuItem "Display Sales")))))))
    (.add
      (doto (JMenu. "View")
        (.add (doto (JMenuItem. "Admin")
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Admin")
                      (admin/display-admin-table)
                      )))))
        (.add (doto (JMenuItem. "Inventory")
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Inventory")
                      ; (tbl/model)
                      ;(tbl/admin-table)
                      )))))
        (.add (doto (JMenuItem. "Sales")
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Sales")
                      ; (tbl/model)
                      ;(tbl/admin-table)
                      )))))))
    (.add
      (doto (JMenu. "Help")
        (.add
          (doto
            (JMenuItem. "About")))))))

;; call frame
(defn exec-main-frame []
  (println "call frame triggered....")
  ;; main frame
  (def main-frame
    (doto
      (new JFrame "Infinite")
      (.setDefaultCloseOperation JFrame/HIDE_ON_CLOSE)
      (.setJMenuBar menuBar)
      (.setVisible true)
      (.setSize 1000 400)
      (.setLocationRelativeTo nil))))

;(exec-main-frame)

