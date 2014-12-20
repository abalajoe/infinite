;;; namespace & libs
(ns infinite.core
  (:import (javax.swing JOptionPane)
           (javax.swing JFrame JLabel JDialog JPanel JComboBox JTextField JButton JOptionPane BorderFactory)
           (java.awt.event ActionListener ItemListener)
           (java.awt GridBagLayout Insets GridLayout)
           (java.awt Dimension))
  (:require [clojure.tools.logging :as log]
            [model.db :as db]))

;; main frame
(def frame)

;; labels
(def username-label (new JLabel "Username"))
(def password-label (new JLabel "Password"))

;; textfields
(def username-field (doto (new JTextField)(.setColumns 25)))
(def password-field (doto (new JTextField)(.setColumns 25)))

;; buttons
(def login-button (doto (new JButton "Login")(.setPreferredSize (new Dimension 70 20))))
(def cancel-button (doto (new JButton "Cancel")(.setPreferredSize (new Dimension 70 20))))
(def exit-button (doto (new JButton "Exit")(.setPreferredSize (new Dimension 70 20))))

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

;; login button action
(. login-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Login Button Pressed")
       ; get username and password entered by user
       (let [username (. username-field (getText))
             password (. password-field (getText))]
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
(. cancel-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Cancel Button Pressed")
       )))

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
    (grid-bag-layout
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
      :gridx 1, :gridy 2
      login-button
      :gridx 1, :gridy 3
      cancel-button
      :gridx 1, :gridy 4
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