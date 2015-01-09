;;; namespace & libs
(ns infinite.core
  (:import (javax.swing JOptionPane)
           (javax.swing JFrame JLabel JPanel JTextField JButton JOptionPane BorderFactory)
           (java.awt.event ActionListener)
           (java.awt GridBagLayout Insets)
           (java.awt Dimension))
  (:require [clojure.tools.logging :as log]
            [model.db :as db]
            [util.utils :as utl]
            [infinite.main :as main]))

;; main frame
(def login-frame)

;; username
(def username (atom ""))

;; labels
(def username-label (new JLabel "Username"))
(def password-label (new JLabel "Password"))
(def user-type-label (new JLabel "User"))

;; textfields
(def username-field (doto (new JTextField)(.setColumns 25)))
(def password-field (doto (new JTextField)(.setColumns 25)))

;; buttons
(def login-button (doto (new JButton "Login")(.setPreferredSize (new Dimension 70 20))))
(def cancel-button (doto (new JButton "Cancel")(.setPreferredSize (new Dimension 70 20))))
(def exit-button (doto (new JButton "Exit")(.setPreferredSize (new Dimension 70 20))))

;; combobox
(def user-type-combo (doto (new JComboBox (java.util.Vector. (db/get-user-type)))(.setPreferredSize (new Dimension 250 20))))

(defn clear-login-fields
  "Function clears login text fields"
  []
  (.setText username-field "")                              ; clear username field
  (.setText password-field ""))                             ; clear password field

;; login button action
(. login-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Login Button Pressed")
       ; get username and password entered by user
       (let [username (.getText username-field)
             ;user-name (reset! username (.getText username-field))
             password (.getText password-field)
             user-type (.getSelectedItem user-type-combo)]
         ; check if the user enters all fields
         (if (or (empty? username)(empty? password))
           (do
             (clear-login-fields)
             (JOptionPane/showMessageDialog
               nil "Please enter all details!" "Enter all Fields"
               JOptionPane/INFORMATION_MESSAGE))
           (do
             ; check if the credentials are correct
             (cond
               (= (db/login-test username password user-type ) 1)
               (do
                 ;(main/status 1)
                 ; log operation to db
                 (if (= user-type "admin")
                   (do (if (= (db/insert-login-logs username) (list 1))
                         (log/infof "successfully logged in %s to database" username)
                         (log/errorf "failed to log in %s to database" username))
                       ; close current jframe
                       (.dispose login-frame)
                       ; open main frame
                       (main/exec-main-frame)
                       (log/infof "successfully logged in %s" username))
                   (do
                     ;(main/status 1)
                     ; log operation to db
                     (if (= (db/insert-login-logs username) (list 1))
                       (log/infof "successfully logged in %s to database" username)
                       (log/errorf "failed to log in %s to database" username))
                     ; close current jframe
                     (.dispose login-frame)
                     ; open main frame
                     (main/exec-user-frame)
                     (log/infof "successfully logged in %s" username))))
              ; (= (db/login-test username password user-type ) 1)
              #_ (do
                 ;(main/status 1)
                 ; log operation to db
                 (if (= (db/insert-login-logs username) (list 1))
                   (log/infof "successfully logged in %s to database" username)
                   (log/errorf "failed to log in %s to database" username))
                 ; close current jframe
                 (.dispose login-frame)
                 ; open main frame
                 (main/exec-user-frame)
                 (log/infof "successfully logged in %s" username))
               :else (do (log/info "credentials incorrect!!")
                          (clear-login-fields)
                          (JOptionPane/showMessageDialog
                            nil "Incorrect Credentials!" "Login usuccessful"
                            JOptionPane/ERROR_MESSAGE))
               )))))))

;; change password button action
(. cancel-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (clear-login-fields)
       (log/info "Cancel Button Pressed"))))

;; exit button action
(. exit-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Exit Button Pressed")
       ; terminate application
       (System/exit 0))))

;; login panel
(def login-panel
  ; use gridbaglayout as layout engine
  (doto (JPanel. (GridBagLayout.))
    ; set title for panel
    (.setBorder(BorderFactory/createTitledBorder "Login"))
    ; style the components
    (utl/grid-bag-layout
      :fill :BOTH, :insets (Insets. 5 1 1 5)
      :gridx 0, :gridy 0, :anchor :LINE_START
      username-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 0,:anchor :LINE_END
      username-field
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 1, :anchor :LINE_START
      password-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 1,:anchor :LINE_END
      password-field
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 2, :anchor :LINE_START
      user-type-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 2,:anchor :LINE_END
      user-type-combo
      :gridx 1, :gridy 3
      login-button
      :gridx 1, :gridy 4
      cancel-button
      :gridx 1, :gridy 5
      exit-button)))

;; login frame
(def login-frame
  (doto (JFrame. "INFINITE INVENTORY SYSTEM")
    ; set login panel to frame
    (.setContentPane login-panel)
    ; set size of frame
    (.setSize 800 300)
    ; make frame visible
    (.setVisible true)
    ; position frame at center of screen
    (.setLocationRelativeTo nil)))