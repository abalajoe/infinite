(ns infinite.test)

;;;;;;; CORE ;;;;;;;;;;
;;; namespace & libs
(ns infinite.core
  (:import (javax.swing JOptionPane)
           (javax.swing JFrame JLabel JDialog JPanel JComboBox JTextField JButton JOptionPane)
           (java.awt.event ActionListener ItemListener)
           (java.awt GridLayout FlowLayout)
           (java.awt Dimension))
  (:require [clojure.tools.logging :as log]
            [model.db :as db]))

;; main frame
(def frame)

;; labels
(def username-label (new JLabel "username"))
(def password-label (new JLabel "password"))

;; textfields
(def username-field (doto (new JTextField)(.setColumns 15)))
(def password-field (new JTextField))

;; buttons
;(def login-button (new JButton "Login"))
(def login-button (doto (new JButton "Login")(.setPreferredSize (new Dimension 150 30))))
(def change-password-button (doto (new JButton "Login")(.setPreferredSize (new Dimension 150 30))))
(def exit-button (doto (new JButton "Login")(.setPreferredSize (new Dimension 150 30))))
;(def change-password-button (new JButton "Password"))
;(def exit-button (new JButton "Exit"))

;; combo box
(def combox
  (let [combobox (JComboBox. (java.util.Vector. ['a 'b 'c]))]
    (.addItemListener combobox
                      (proxy [ItemListener] []
                        (itemStateChanged [item-event]
                          (println (str "selection changed to "
                                        (.getSelectedItem combobox))))))))
(def combo-values ["joe" "abala"])
;(def combobox (new JComboBox(java.util.Vector. (db/get-brands) )))

;; login button action
(. login-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Login Button Pressed")
       ; get username and password entered by user
       (let [username (. username-field (getText)) password (. password-field (getText))]
         ; check if the user enters all fields
         (if (or (empty? username)(empty? password))
           (JOptionPane/showMessageDialog
             nil "Please enter all details!" "Enter all Fields"
             JOptionPane/INFORMATION_MESSAGE)
           (do
             ; check if the credentials are correct
             (if (= (db/login username password ) 1)
               (do
                 ; close current jframe
                 (.dispose frame)
                 ; open main frame
                 ; (mn/call-frame)
                 (log/info "success")
                 )
               (do (log/info "credentials incorrect!!")
                   (JOptionPane/showMessageDialog
                     nil "Incorrect Credentials!" "Login usuccessful"
                     JOptionPane/ERROR_MESSAGE)))))))))

;; change password button action
(. change-password-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Password Button Pressed"))))

;; exit button action
(. exit-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Exit Button Pressed")
       ; terminate application
       (System/exit 0))))

;; the top panel
(def top-panel
  (doto
    (JPanel.)
    ; set the layout of the panel
    (.setLayout (new GridLayout 2 2))
    ; set the size of the panel
    (.setPreferredSize (new Dimension 600 100))
    ; add components to panel
    (.add username-label)
    ;(.add combobox)
    (.add username-field)
    (.add password-label)
    (.add password-field)))

;; the bottom panel
(def bottom-panel
  (doto
    (JPanel.)
    ; set the layout of the panel
    (.setLayout (new FlowLayout))
    ; set the size of the panel
    (.setPreferredSize (new Dimension 600 50))
    ; add components to the panel
    (.add login-button )
    (.add change-password-button)
    (.add exit-button)))
;btn.setPreferredSize(new Dimension(40, 40));
;; main frame
(def frame
  (doto
    ; initialize jframe
    (new JFrame "Duste Inventory")
    (log/info "initializing frame..")
    ; set layout
    (.setLayout (new GridLayout 2 1 3 3))
    ; set closing frame behaviour
    (.setDefaultCloseOperation JFrame/HIDE_ON_CLOSE)
    ; add components
    (.add top-panel)
    (.add bottom-panel)
    ; package components
    (.setSize 600 200)
    ; display frame
    (.setVisible true)
    ; place frame in the middle of screen
    (.setLocationRelativeTo nil)))

(defn -main
  "The application's main function"
  [& args]
  frame)

(-main)
