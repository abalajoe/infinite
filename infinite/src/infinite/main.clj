
(ns infinite.main
  (:import (javax.swing JFrame JMenu JMenuBar JMenuItem JTable)
           (java.awt.event ActionListener KeyEvent))
  (:require [clojure.tools.logging :as log]
            [layout.admin :as admin]
            [util.utils :as utl]
            [model.db :as db]))

;; main frame
(def main-frame)

;; menubar
(def menuBar
  (doto (JMenuBar.)
    (.add
      (doto (JMenu. "File")
        (.setMnemonic KeyEvent/VK_F)
        (.setToolTipText "File")
            (.add
              (doto
                (JMenuItem. "Admin")
                (.setToolTipText "Admin")
                (.setMnemonic KeyEvent/VK_A)))
            (.add
              (doto
                (JMenuItem. "Inventory")
                (.setToolTipText "Inventory")
                (.setMnemonic KeyEvent/VK_I)
                ))
            (.add (JMenuItem. "Suppliers"))
            (.add (JMenuItem. "Expenditure"))
            (.add (JMenuItem. "Budget"))
            (.add (JMenuItem. "Graphs"))
            (.add (JMenuItem. "Reports"))))
    (.add
      (doto (JMenu. "Edit")
        (.setToolTipText "Edit")
        (.setMnemonic KeyEvent/VK_E)
            (.add
              (doto (JMenu. "Admin")
                (.setToolTipText "Admin")
                (.setMnemonic KeyEvent/VK_A)
                (.add (doto (JMenuItem. "Add Admin")
                        (.setToolTipText "Add Admin")
                        (.setMnemonic KeyEvent/VK_A)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Add Admin")
                              (admin/exec-admin-frame)
                              )))))
                (.add (doto (JMenuItem. "Edit Admin")
                        (.setToolTipText "Edit Admin")
                        (.setMnemonic KeyEvent/VK_E)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Edit Admin")
                              ; display table to edit admin
                              (utl/display-table (db/list-admin) "Edit Admin" utl/edit-admin-table)
                              )))))))
            (.add
              (doto
                (new JMenu "Inventory")
                (.setToolTipText "Inventory")
                (.setMnemonic KeyEvent/VK_I)
                (.add
                  (doto
                    (new JMenuItem "Add Inventory")
                    (.setToolTipText "Add Inventory")
                    (.setMnemonic KeyEvent/VK_A)
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Add Inventory")
                          ;(pnl/inventory-frame)
                          )))))
                (.add
                  (doto
                    (new JMenuItem "Display Inventory")
                    (.setToolTipText "Display Inventory")
                    (.setMnemonic KeyEvent/VK_D)
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Display Inventory")
                          ;(tbl/inventory-table)
                          )))))
                (.add
                  (doto
                    (new JMenu "Inventory Analysis")
                    (.setToolTipText "Inventory Analysis")
                    (.setMnemonic KeyEvent/VK_I)
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
                        (.setToolTipText "Brand")
                        (.setMnemonic KeyEvent/VK_B)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (log/info "Brand analysis")
                              ; (chart/brand-piechart)
                              )))))
                    (.add
                      (doto
                        (new JMenuItem "Size")
                        (.setToolTipText "Size")
                        (.setMnemonic KeyEvent/VK_S)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (log/info "Size analysis")
                              ; (chart/size-piechart)
                              )))))))))
            (.add
              (doto
                (new JMenu "Sales")
                (.setMnemonic KeyEvent/VK_S)
                (.add
                  (doto
                    (new JMenuItem "Record Sales")
                    (.setToolTipText "Record Sales")
                    (.setMnemonic KeyEvent/VK_R)
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
                    (new JMenuItem "Display Sales")
                    (.setToolTipText "Display Sales")
                    (.setMnemonic KeyEvent/VK_D)
                    ))))))
    (.add
      (doto (JMenu. "View")
        (.setMnemonic KeyEvent/VK_V)
        (.add (doto (JMenuItem. "Admin")
                (.setToolTipText "Admin")
                (.setMnemonic KeyEvent/VK_A)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Admin")
                      ; display admin table
                      (utl/display-table (db/list-admin) "Display admin" (JTable.))
                      )))))
        (.add (doto (JMenuItem. "Inventory")
                (.setToolTipText "Inventory")
                (.setMnemonic KeyEvent/VK_I)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Inventory")
                      ; (tbl/model)
                      ;(tbl/admin-table)
                      )))))
        (.add (doto (JMenuItem. "Sales")
                (.setToolTipText "Sales")
                (.setMnemonic KeyEvent/VK_S)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Sales")
                      ; (tbl/model)
                      ;(tbl/admin-table)
                      )))))))
    (.add
      (doto (JMenu. "Audit Trails")
        (.setMnemonic KeyEvent/VK_A)
        (.add (doto (JMenuItem. "Login logs")
                (.setToolTipText "Login logs")
                (.setMnemonic KeyEvent/VK_L)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Login Logs")
                      ; display login logs table
                      (utl/display-table (db/list-login-logs) "Login logs" (JTable.))
                      )))))
        (.add (doto (JMenuItem. "Audit logs")
                (.setToolTipText "Audit logs")
                (.setMnemonic KeyEvent/VK_A)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Audit Logs")
                      ; display audit logs table
                      (utl/display-table (db/list-audit-logs) "Audit logs" (JTable.))
                      )))))))
    (.add
      (doto (JMenu. "Help")
        (.setToolTipText "Help")
        (.setMnemonic KeyEvent/VK_H)
        (.add
          (doto
            (JMenuItem. "About")
            (.setToolTipText "About")
            (.setMnemonic KeyEvent/VK_A)))))))

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

