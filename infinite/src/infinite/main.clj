
(ns infinite.main
  (:import (javax.swing JFrame JMenu JMenuBar JMenuItem JTable BorderFactory JPanel)
           (java.awt.event ActionListener KeyEvent)
           (org.jfree.chart ChartPanel))
  (:require [clojure.tools.logging :as log]
            [layout.admin :as admin]
            [util.utils :as utl]
            [model.db :as db])
  (:use [incanter core charts pdf stats io datasets]))

;; main frame
(def main-frame)

;; days of the week
(def days ["Mon" "Tue" "Wed" "Thur" "Fri" "Sat" "Sun"])

;; total sales
(def sale [10 20 30 40 50 60 70])
(def vodka [11 21 31 41 51 61 71])
(def whisky [13 23 33 43 53 63 73])
(def gin [15 25 35 45 55 65 75])
(def brandy [17 27 37 47 57 67 77])
(def beer [19 29 39 49 59 69 79])
(def beverage [20 30 40 50 60 70 80])

(def plot (line-chart days
                      vodka
                      :legend true
                      :series-label "Vodka"))

;; menubar
(def menuBar
  (doto (JMenuBar.)
    (.add
      (doto (JMenu. "File")
        (.setMnemonic KeyEvent/VK_F)                        ; set the mnemonic of the menu
        (.setToolTipText "File")                            ; set tool tip text of the menu
            (.add                                           ; add menu item
              (doto
                (JMenuItem. "Print")                        ; create menu item with lable
                (.setMnemonic KeyEvent/VK_P)                ; set mnemonic of menu item
                (.setToolTipText "Print")))                 ; set tool tip text of menu item
            (.add
              (doto
                (JMenuItem. "Exit")
                (.setToolTipText "Exit")
                (.setMnemonic KeyEvent/VK_E)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Exit Application")
                      (System/exit 0))))))))
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
      (doto (JMenu. "Data Analysis")
        (.setToolTipText "Data Analysis")
        (.setMnemonic KeyEvent/VK_D)
        (.add
          (doto
            (JMenuItem. "About")
            (.setToolTipText "About")
            (.setMnemonic KeyEvent/VK_A)))))
    (.add
      (doto (JMenu. "Reports")
        (.setToolTipText "Reports")
        (.setMnemonic KeyEvent/VK_R)
        (.add
          (doto
            (JMenuItem. "About")
            (.setToolTipText "About")
            (.setMnemonic KeyEvent/VK_A)))))
    (.add
      (doto (JMenu. "Navigate")
        (.setMnemonic KeyEvent/VK_N)
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

;; Line Chart Panel
(def line-chart-panel
  ; use gridbaglayout as layout engine
  (doto (JPanel.)
    ; set title for panel
    (.setBorder(BorderFactory/createTitledBorder "Line Chart"))
    ; style the components
    #_(.add (ChartPanel. (line-chart days
                                  sale
                                  :legend true
                                  :series-label "Drink")))
    (.add (ChartPanel.(add-categories plot days whisky :series-label "Whisky")))
    (.add (ChartPanel.(add-categories plot days brandy :series-label "Brandy")))
    (.add (ChartPanel.(add-categories plot days gin :series-label "Gin")))
    (.add (ChartPanel.(add-categories plot days beer :series-label "Beer")))
    (.add (ChartPanel.(add-categories plot days beverage :series-label "Beverage")))))



;; call frame
(defn exec-main-frame []
  (println "call frame triggered....")
  ;; main frame
  (def main-frame
    (doto
      (new JFrame "Infinite")
      (.setContentPane line-chart-panel)
      (.setDefaultCloseOperation JFrame/HIDE_ON_CLOSE)
      (.setJMenuBar menuBar)
      (.setVisible true)
      (.setSize 950 550)
      (.setLocationRelativeTo nil))))

;(exec-main-frame)

