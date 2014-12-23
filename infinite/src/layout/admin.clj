(ns layout.admin
  (:import (javax.swing JFrame JLabel JDialog JPanel JTextField
                        JButton JOptionPane BorderFactory AbstractAction
                        JOptionPane JScrollPane SwingUtilities)
           (java.awt.event ActionListener MouseAdapter)
           (java.awt GridBagLayout Insets GridLayout Dimension))
  (:require [clojure.tools.logging :as log]
            [model.db :as db]
            [util.utils :as utl]))

;; admin frame
(def admin-frame)

;; labels
(def username-label (new JLabel "Username"))
(def password-label (new JLabel "Password"))
(def confirm-password-label (new JLabel "Confirm Password"))

;; textfields
(def username-field (doto (new JTextField)(.setColumns 25)))
(def password-field (doto (new JTextField)(.setColumns 25)))
(def confirm-password-field (doto (new JTextField)(.setColumns 25)))

;; buttons
(def add-button (doto (new JButton "Add")(.setPreferredSize (new Dimension 70 20))))
(def cancel-button (doto (new JButton "Cancel")(.setPreferredSize (new Dimension 70 20))))
(def exit-button (doto (new JButton "Exit")(.setPreferredSize (new Dimension 70 20))))

;; add button action
(. add-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Add admin Button Pressed")
       ; get username and password entered by user
       (let [username (.getText username-field)
             password (.getText password-field)
             confirm-password (.getText confirm-password-field)]
         ; check if the user enters all fields
         (if (or (empty? username)(empty? password)(empty? confirm-password))
           (JOptionPane/showMessageDialog
             nil "Please enter all details!" "Enter all Fields"
             JOptionPane/INFORMATION_MESSAGE)
           (do
             ; check if the credentials are correct
             (if (= password confirm-password)
               (do
                 (if (= (db/insert-admin username password) (list 1))
                   (do
                     (log/infof "successfully added %s" username)
                     (JOptionPane/showMessageDialog
                       nil (format "Successfully inserted %s to database" username) "Successful operation"
                       JOptionPane/INFORMATION_MESSAGE)
                     (.setText username-field "")
                     (.setText password-field "")
                     (.setText confirm-password-field ""))
                   (do (log/info "Insert Admin Database Error!!")
                       (JOptionPane/showMessageDialog
                         nil "Failed inserting admin to database!" "Database error"
                         JOptionPane/ERROR_MESSAGE))))
               (do (log/info "password mismatch!!")
                   (JOptionPane/showMessageDialog
                     nil "Passwords do not match!" "Password mismatch"
                     JOptionPane/ERROR_MESSAGE)))))))))

;; change password button action
(. cancel-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (.setText username-field "")                         ; clear username field
       (.setText password-field "")                         ; clear password field
       (.setText confirm-password-field "")                 ; clear confirm password field
       (log/info "Cancel Button Pressed"))))

;; exit button action
(. exit-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Exit Button Pressed")
       ; dispose the frame
       (.dispose admin-frame))))

;; login panel
(def admin-panel
  ; use gridbaglayout as layout engine
  (doto (JPanel. (GridBagLayout.))
    ; set title for panel
    (.setBorder(BorderFactory/createTitledBorder "Add Admin"))
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
      confirm-password-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 2,:anchor :LINE_END
      confirm-password-field
      :gridx 1, :gridy 3
      add-button
      :gridx 1, :gridy 4
      cancel-button
      :gridx 1, :gridy 5
      exit-button)))

;; jdialog
(defn admin-dialog [data-ref]
  ; cleate dialog, labels and text fields
  (let [dialog (new JDialog (@data-ref :owner) true)
        id-label (doto (new JLabel "ID: "))
        username-label (doto (new JLabel "Username: "))
        password-label (doto (new JLabel "Password: "))
        confirm-password-label (doto (new JLabel "Confirm password: "))
        id-field (doto (new JTextField)
                   (.setColumns 25)                         ; set field width
                   (.setText (@data-ref :id))               ; set text to field
                   (.setEditable false))                    ; unable field editing
        username-field (doto (new JTextField)
                         (.setColumns 25)
                         (.setText (@data-ref :username))
                         (.setEditable false))
        password-field (doto (new JTextField)
                         (.setColumns 25)
                         (.setText (@data-ref :password)))
        confirm-password-field (doto (new JTextField)
                           (.setColumns 25))
        update-button (doto (new JButton "Update")
                        (.setPreferredSize (new Dimension 70 20)) ; set button width
                     (.addActionListener
                       (proxy [AbstractAction] []
                         (actionPerformed [evt]
                           (log/infof "Updating admin: [%s][%s]"(@data-ref :username)(@data-ref :id))
                           (if (or (empty? (.getText password-field))(empty? (.getText confirm-password-field)))
                             (JOptionPane/showMessageDialog
                               nil "Please enter all fields!" "Fields"
                               JOptionPane/ERROR_MESSAGE)
                             (do
                               (if (not (= (.getText password-field)(.getText confirm-password-field)))
                                 (JOptionPane/showMessageDialog
                                   nil "Passwords are not the same" "Credentials"
                                   JOptionPane/INFORMATION_MESSAGE)
                                 (do
                                   (let [result (JOptionPane/showConfirmDialog nil, "Are you sure you want to update?", "Confirm Update Operation",
                                                                               JOptionPane/YES_NO_OPTION, JOptionPane/QUESTION_MESSAGE)]
                                     (if (= result JOptionPane/YES_OPTION)
                                       (if (= (db/update-admin (.getText password-field) (@data-ref :id)) (list 1))
                                         (do
                                           (log/infof "[%s]Successfully update " (@data-ref :username))
                                           (JOptionPane/showMessageDialog
                                             nil (str "Successfully updated!" (@data-ref :username))  "update Sussessful"
                                             JOptionPane/INFORMATION_MESSAGE))
                                         (do
                                           (log/infof "[%s]Failed updating " (@data-ref :username))
                                           (JOptionPane/showMessageDialog
                                             nil (str "Failed updating!" (@data-ref :username))  "update Failed"
                                             JOptionPane/ERROR_MESSAGE)))))))))
                           (.dispose dialog)))))
        delete-button (doto (new JButton "Delete")
                        (.setPreferredSize (new Dimension 70 20))
                     (.addActionListener
                       (proxy [AbstractAction] []
                         (actionPerformed [evt]
                           (log/infof "Deleting admin: [%s][%s]"(@data-ref :username)(@data-ref :id))
                           (let [result (JOptionPane/showConfirmDialog nil, "Are you sure you want to delete?", "Confirm Delete Operation",
                                                                       JOptionPane/YES_NO_OPTION, JOptionPane/QUESTION_MESSAGE)]
                             (if (= result JOptionPane/YES_OPTION)
                               (if (= (db/delete-admin (@data-ref :id)) (list 1))
                                 (do
                                   (log/infof "[%s]Successfully deleted " (@data-ref :username))
                                   (JOptionPane/showMessageDialog
                                     nil (str "Successfully deleted!" (@data-ref :username))  "Deletion Sussessful"
                                     JOptionPane/INFORMATION_MESSAGE))
                                 (do
                                   (log/infof "[%s]Failed deleting " (@data-ref :username))
                                   (JOptionPane/showMessageDialog
                                     nil (str "Failed deleting!" (@data-ref :username))  "Deletion Failed"
                                     JOptionPane/ERROR_MESSAGE)))))
                           (.dispose dialog)))))
        exit-button (doto (new JButton "Exit")
                      (.setPreferredSize (new Dimension 70 20))
                        (.addActionListener
                          (proxy [AbstractAction] []
                            (actionPerformed [evt]
                              (log/info "exit")
                              (.dispose dialog)))))]
   (doto dialog
     ; set dialog layout
      (.setLayout (new GridBagLayout))
      ; style the components
      (utl/grid-bag-layout
        :fill :BOTH, :insets (Insets. 5 1 1 5)
        :gridx 0, :gridy 0, :anchor :LINE_START
        id-label
        :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
        :gridx 1, :gridy 0,:anchor :LINE_END
        id-field
        :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
        :gridx 0, :gridy 1, :anchor :LINE_START
        username-label
        :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
        :gridx 1, :gridy 1,:anchor :LINE_END
        username-field
        :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
        :gridx 0, :gridy 2, :anchor :LINE_START
        password-label
        :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
        :gridx 1, :gridy 2,:anchor :LINE_END
        password-field
        :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
        :gridx 0, :gridy 3, :anchor :LINE_START
        confirm-password-label
        :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
        :gridx 1, :gridy 3,:anchor :LINE_END
        confirm-password-field
        :gridx 1, :gridy 4
        update-button
        :gridx 1, :gridy 5
        delete-button
        :gridx 1, :gridy 6
        exit-button)
      (.setSize 500 200)
      (.setLocationRelativeTo nil)
      (.setVisible true))
    (@data-ref :username)))

(defn dialog-string [owner id username password]
  (admin-dialog (ref {:owner owner :id id :username username :password password})))

;; edit table mouse listener event
(.addMouseListener utl/edit-admin-table
                   (proxy [MouseAdapter] []
                     (mouseClicked [e]
                       (when (== (.getClickCount e) 1)
                         (let [coordinates (.getPoint e)
                               row-count (.rowAtPoint utl/edit-admin-table coordinates)
                               column-count (.columnAtPoint utl/edit-admin-table coordinates)]
                           (prn "click r > " row-count)
                           (prn "click c > " column-count)
                           (prn "ID > " (:username(nth (db/list-admin)row-count)))
                           (let [id (:id(nth (db/list-admin)row-count))
                                 username (:username(nth (db/list-admin)row-count))
                                 password (:password(nth (db/list-admin)row-count))]
                             (dialog-string nil (str id) username password))
                           (flush))))))

(defn edit-admin-table []
  "This table shows all the administrators
  for the system and allows editing of data"
  (def rows (db/list-admin))
  (. SwingUtilities invokeLater
     (fn []
       (doto (JFrame. "Edit Admin")
         (.setDefaultCloseOperation (. JFrame HIDE_ON_CLOSE))
         (.setContentPane
           (doto (JPanel. (GridLayout. 1 0))
             (.setOpaque true)
             (.add (JScrollPane.
                     (doto utl/edit-admin-table
                       (.setModel (utl/model rows
                                         (vec (map str (keys (first rows))))
                                         (fn [r c] ((nth rows r) (nth (keys (first rows)) c)))))
                       (.setPreferredScrollableViewportSize
                         (Dimension. 800 300))
                       (.setFillsViewportHeight true))))))
         (.setSize 1000 300)
         (.setLocationRelativeTo nil)
         (.setVisible true)))))

(defn display-admin-table []
  "This table shows all the administrators
  for the system and does not allow editing"
  (def rows (db/list-admin))
  (. SwingUtilities invokeLater
     (fn []
       (doto (JFrame. "Display Admin")
         (.setDefaultCloseOperation (. JFrame HIDE_ON_CLOSE))
         (.setContentPane
           (doto (JPanel. (GridLayout. 1 0))
             (.setOpaque true)
             (.add (JScrollPane.
                     (doto utl/show-admin-table
                       (.setModel (utl/model rows
                                             (vec (map str (keys (first rows))))
                                             (fn [r c] ((nth rows r) (nth (keys (first rows)) c)))))
                       (.setPreferredScrollableViewportSize
                         (Dimension. 800 300))
                       (.setFillsViewportHeight true))))))
         (.setSize 1000 300)
         (.setLocationRelativeTo nil)
         (.setVisible true)))))

;(admin-table)

;; admin frame
(defn exec-admin-frame
  "Function exposes admin frame"
  []
  (def admin-frame
    (doto (JFrame. "INFINITE INVENTORY SYSTEM")
      ; set login panel to frame
      (.setContentPane admin-panel)
      ; set size of frame
      (.setSize 800 300)
      ; make frame visible
      (.setVisible true)
      ; position frame at center of screen
      (.setLocationRelativeTo nil))))
