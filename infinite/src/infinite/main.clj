
(ns infinite.main
  (:import (javax.swing JFrame JMenu JMenuBar JMenuItem JTable BorderFactory JPanel JOptionPane JTextArea)
           (java.awt.event ActionListener KeyEvent MouseListener)
           (org.jfree.chart ChartPanel))
  (:require [clojure.tools.logging :as log]
            [layout.admin :as admin]
            [layout.sales :as sales]
          ;  [layout.help :as help]
            [layout.reports :as reports]
            [layout.analysis :as analysis]
            [util.utils :as utl]
            [model.db :as db])
  (:use [incanter core charts pdf stats io datasets]))

;; main frame
(def main-frame)

;; admin or user
(def status (atom nil))

;; main frame jframe
(def frame (JFrame. "Infinite Inventory"))

;; days of the week
(def days ["Mon" "Tue" "Wed" "Thur" "Fri" "Sat" "Sun"])

;; total sales
(def Drinks (db/get-vodka-line-chart)); this is vodka details, i have labeled it Drinks so that the y-axis will be laeled that way
(def whisky (db/get-whisky-line-chart))
(def gin (db/get-gin-line-chart))
(def brandy (db/get-brandy-line-chart))
(def beer (db/get-beer-line-chart))
(def beverage (db/get-beverage-line-chart))

(def plot (line-chart days
                      Drinks
                      :legend true
                      :series-label "vodka"))

;; menubar
(def user-menuBar
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
          (doto (JMenu. "Sales")
            (.setToolTipText "Sales")
            (.setMnemonic KeyEvent/VK_A)
            (.add (doto (JMenuItem. "Add Sales")
                    (.setToolTipText "Add Sales")
                    (.setMnemonic KeyEvent/VK_A)
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (println "Add Sales")
                          (sales/exec-sales-frame)
                          )))))
            (.add (doto (JMenuItem. "Edit Sales")
                    (.setToolTipText "Edit Sales")
                    (.setMnemonic KeyEvent/VK_E)
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (println "Edit Admin")
                          ; display table to edit admin
                          (utl/display-table (db/list-sales) "Edit Sales" utl/edit-sales-table)
                          )))))))))
    (.add
      (doto (JMenu. "View")
        (.setMnemonic KeyEvent/VK_V)
        (.add (doto (JMenuItem. "Inventory")
                (.setToolTipText "Inventory")
                (.setMnemonic KeyEvent/VK_I)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Inventory")
                      ; (tbl/model)
                      (utl/display-table (db/list-inventory) "Display Inventory" (JTable.))
                      )))))
        (.add (doto (JMenuItem. "Sales")
                (.setToolTipText "Sales")
                (.setMnemonic KeyEvent/VK_S)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Sales")
                      ; (tbl/model)
                      (utl/display-table (db/list-sales) "Display admin" (JTable.))
                      )))))))
    (.add
      (doto (JMenu. "Data Analysis")
        (.setToolTipText "Data Analysis")
        (.setMnemonic KeyEvent/VK_D)
        (.add
          (doto
            (JMenuItem. "Liquor Taste")
            (.setToolTipText "Liquor Taste")
            (.setMnemonic KeyEvent/VK_L)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Taste")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Taste" (db/liquor-names))
                  )))))
        (.add
          (doto
            (JMenuItem. "Liquor Brands")
            (.setToolTipText "Liquor Brand")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Brand")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Brands" (db/liquor-brand))
                  )))))
        (.add
          (doto
            (JMenuItem. "Liquor Size")
            (.setToolTipText "Liquor Size")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Size")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Size" (db/liquor-size))
                  )))))))
    (.add
      (doto (JMenu. "Help")
        (.setToolTipText "Help")
        (.setMnemonic KeyEvent/VK_H)
        (.add
          (doto
            (JMenuItem. "About")
            (.setToolTipText "About")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  ; (help/dialog-string nil)
                  (analysis/liquor-piechart)
                  )))))))))

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
                    (new JMenuItem "Edit Inventory")
                    (.setToolTipText "Edit Inventory")
                    (.setMnemonic KeyEvent/VK_E)
                    (.addActionListener
                      (proxy [ActionListener] []
                        (actionPerformed [e]
                          (log/info "Edit Inventory")
                          (utl/display-table (db/list-inventory) "Edit test" utl/test-table)
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
              (doto (JMenu. "Sales")
                (.setToolTipText "Sales")
                (.setMnemonic KeyEvent/VK_A)
                (.add (doto (JMenuItem. "Add Sales")
                        (.setToolTipText "Add Sales")
                        (.setMnemonic KeyEvent/VK_A)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Add Sales")
                              (sales/exec-sales-frame)
                              )))))
                (.add (doto (JMenuItem. "Edit Sales")
                        (.setToolTipText "Edit Sales")
                        (.setMnemonic KeyEvent/VK_E)
                        (.addActionListener
                          (proxy [ActionListener] []
                            (actionPerformed [e]
                              (println "Edit Admin")
                              ; display table to edit admin
                              (utl/display-table (db/list-sales) "Edit Sales" utl/edit-sales-table)
                              )))))))))
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
                      (utl/display-table (db/list-inventory) "Display Inventory" (JTable.))
                      )))))
        (.add (doto (JMenuItem. "Sales")
                (.setToolTipText "Sales")
                (.setMnemonic KeyEvent/VK_S)
                (.addActionListener
                  (proxy [ActionListener] []
                    (actionPerformed [e]
                      (log/info "Show Sales")
                      ; (tbl/model)
                      (utl/display-table (db/list-sales) "Display admin" (JTable.))
                      )))))))
    (.add
      (doto (JMenu. "Data Analysis")
        (.setToolTipText "Data Analysis")
        (.setMnemonic KeyEvent/VK_D)
        (.add
          (doto
            (JMenuItem. "Liquor Taste")
            (.setToolTipText "Liquor Taste")
            (.setMnemonic KeyEvent/VK_L)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Taste")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Taste" (db/liquor-names))
                  )))))
        (.add
          (doto
            (JMenuItem. "Liquor Brands")
            (.setToolTipText "Liquor Brand")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Brand")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Brands" (db/liquor-brand))
                  )))))
        (.add
          (doto
            (JMenuItem. "Liquor Size")
            (.setToolTipText "Liquor Size")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Liquor Size")
                  ; (tbl/model)
                  (utl/dialog-string nil "Liquor Size" (db/liquor-size))
                  )))))))
    (.add
      (doto (JMenu. "Reports")
        (.setToolTipText "Reports")
        (.setMnemonic KeyEvent/VK_R)
        (.add
          (doto
            (JMenuItem. "Admin")
            (.setToolTipText "Admin Report")
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Admin Report")
                  ; if returned value is not equals to false
                  (if (not (= false (reports/generate-pdf
                                    "Administrator List"
                                    reports/admin-template
                                    reports/admin-report-data
                                    "E:\\infinitereports\\admin.pdf"
                                    "ID" "USERNAME" "PASSWORD" "TYPE")))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generated successfully" "Report generation successful"
                           JOptionPane/INFORMATION_MESSAGE))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generation failed" "Report generation unsuccessful"
                           JOptionPane/ERROR_MESSAGE)))
                  )))))
        (.add
          (doto
            (JMenuItem. "Inventory")
            (.setToolTipText "Inventory Report")
            (.setMnemonic KeyEvent/VK_I)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Admin Report")
                  ; if returned value is not equals to false
                  (if (not (= false (reports/generate-pdf
                                      "Inventory List"
                                      reports/inventory-template
                                      reports/inventory-report-data
                                      "E:\\infinitereports\\inventory.pdf"
                                      "ID" "LIQUOR" "BRAND" "SIZE" "QUANTITY" "PRICE" "DATE")))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generated successfully" "Report generation successful"
                           JOptionPane/INFORMATION_MESSAGE))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generation failed" "Report generation unsuccessful"
                           JOptionPane/ERROR_MESSAGE)))
                  )))))
        (.add
          (doto
            (JMenuItem. "Sales")
            (.setToolTipText "Sales Report")
            (.setMnemonic KeyEvent/VK_S)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (log/info "Sales Report")
                  ; if returned value is not false
                  (if (not (= false (reports/generate-pdf
                                      "Sales List"
                                      reports/sales-template
                                      reports/sales-report-data
                                      "E:\\infinitereports\\sales.pdf"
                                      "ID" "PRODUCT" "SIZE" "DATE ADkDED")))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generated successfully" "Report generation successful"
                           JOptionPane/INFORMATION_MESSAGE))
                    (do  (JOptionPane/showMessageDialog
                           nil "Report generation failed" "Report generation unsuccessful"
                           JOptionPane/ERROR_MESSAGE)))
                  )))))))
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
            (.setMnemonic KeyEvent/VK_A)
            (.addActionListener
              (proxy [ActionListener] []
                (actionPerformed [e]
                  (JOptionPane/showMessageDialog
                    nil "Infinite is an Inventory management software\n solutions that keeps track of inventory,\n sales and purchasing." "App Info"
                    JOptionPane/INFORMATION_MESSAGE)
                  )))))))))

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
      frame
      (.setContentPane line-chart-panel)
      (.setDefaultCloseOperation JFrame/HIDE_ON_CLOSE)
      (.setJMenuBar menuBar)
      (.setVisible true)
      (.setSize 950 520)
      (.setLocationRelativeTo nil))))

;; call frame
(defn exec-user-frame []
  (println "call frame triggered....")
  ;; main frame
  (def main-frame
    (doto
      frame
      (.setContentPane line-chart-panel)
      (.setDefaultCloseOperation JFrame/HIDE_ON_CLOSE)
      (.setJMenuBar user-menuBar)
      (.setVisible true)
      (.setSize 950 510)
      (.setLocationRelativeTo nil))))

;(exec-main-frame)

