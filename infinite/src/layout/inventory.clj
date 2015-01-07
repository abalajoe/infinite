(ns layout.inventory
  (:import (javax.swing JFrame JLabel JDialog JPanel JTextField
                        JButton JOptionPane BorderFactory AbstractAction
                        JOptionPane JComboBox)
           (java.awt.event ActionListener MouseAdapter)
           (java.awt GridBagLayout Insets Dimension)
           (java.sql SQLException))
  (:require [clojure.tools.logging :as log]
            [model.db :as db]
            [util.utils :as utl]))

;; inventory frame
(def inventory-frame)

;; labels
(def product-label (new JLabel "Product"))
(def brand-label (new JLabel "Brand"))
(def size-label (new JLabel "Size"))
(def quantity-label (new JLabel "Quantity"))
(def price-label (new JLabel "Price"))

;; text fields
(def product-combobox (new JComboBox (java.util.Vector. (db/get-liqours))))
(def brand-combobox (new JComboBox(java.util.Vector. (db/get-brands))))
(def size-combobox (new JComboBox(java.util.Vector. (db/get-size))))
(def quantity-field (doto (new JTextField)(.setColumns 25)))
(def price-field (doto (new JTextField)(.setColumns 25)))

;; buttons
(def add-button (doto (new JButton "Add")(.setPreferredSize (new Dimension 70 20))))
(def cancel-button (doto (new JButton "Cancel")(.setPreferredSize (new Dimension 70 20))))
(def exit-button (doto (new JButton "Exit")(.setPreferredSize (new Dimension 70 20))))

(defn clear-add-inventory-fields
  "Function clears add-admin text fields"
  []
  (.setText quantity-field "")                              ; clear username field
  (.setText price-field ""))                     ; clear confirm password field

(. add-button addActionListener                             ;; add listener to save button
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Inventory Save Button Pressed")
       (let [liquor (.getSelectedItem product-combobox)      ; get liquor
             brand (.getSelectedItem brand-combobox)        ; get brand
             size (.getSelectedItem size-combobox)          ; get size
             price (.getText price-field)           ;; get the price and convert to long
             quantity (.getText quantity-field)]         ;; get the quantity and convert to long
         (if (or (empty? price)                          ;; check if all the fields have been populated
                 (empty? quantity))
           (JOptionPane/showMessageDialog                   ;; display dialog if either field has been left empty
             nil "Enter Fields" "Enter All Fields"
             JOptionPane/INFORMATION_MESSAGE)
           (do
             (let [quantity-long (utl/convert-to-long quantity)
                   price-long (utl/convert-to-long price)]
               (if (and (number? quantity-long) (number? price-long)) ; check if user input is valid number
                 (do
                   (try
                     ; confirm save operation
                     (let [result (JOptionPane/showConfirmDialog nil, "Do you want to save?", "Confirm Save Operation",
                                                                 JOptionPane/YES_NO_OPTION, JOptionPane/QUESTION_MESSAGE)]
                       (if (= result JOptionPane/YES_OPTION)
                         (do
                           (log/infof "New inventory added:> [%s][%s][%s][%s][%s]" liquor brand size price quantity)
                           (db/insert-inventory liquor brand size price-long quantity-long)
                           (clear-add-inventory-fields)     ; clear fields
                           (JOptionPane/showMessageDialog
                             nil "Inventory inserted successfully :)" "Successful"
                             JOptionPane/INFORMATION_MESSAGE))
                         (do
                           (log/infof "Cancelling addition of inentory:> [%s][%s][%s][%s][%s]" liquor brand size price quantity)
                           (clear-add-inventory-fields)     ;clear fields
                           )))
                     (catch SQLException e
                       (log/error "Add Inventory SQLE => " (.getMessage e))
                       (JOptionPane/showMessageDialog                   ;; display dialog if either field has been left empty
                         nil "Problem with database!" "Database"
                         JOptionPane/ERROR_MESSAGE))
                     (catch Exception e
                       (log/error "Add Inventory E => " (.getMessage e))
                       (JOptionPane/showMessageDialog                   ;; display dialog if either field has been left empty
                         nil "Could not add inventory to database!" "Operation Failed"
                         JOptionPane/ERROR_MESSAGE))))
                 (log/error "User not putting valid entry in add-inventory panels for price and quantity")
                 ))))))))

;; change password button action
(. cancel-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (.setText quantity-field "")                         ; clear username field
       (.setText price-field "")                 ; clear confirm password field
       (log/info "Cancel Button Pressed"))))

;; exit button action
(. exit-button addActionListener
   (proxy [ActionListener] []
     (actionPerformed [e]
       (log/info "Exit Button Pressed")
       ; dispose the frame
       (.dispose inventory-frame))))

;; login panel
(def inventory-panel
  ; use gridbaglayout as layout engine
  (doto (JPanel. (GridBagLayout.))
    ; set title for panel
    (.setBorder(BorderFactory/createTitledBorder "Add Inventory"))
    ; style the components
    (utl/grid-bag-layout
      :fill :BOTH, :insets (Insets. 5 1 1 5)
      :gridx 0, :gridy 0, :anchor :LINE_START
      product-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 0,:anchor :LINE_END
      product-combobox
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 1, :anchor :LINE_START
      brand-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 1,:anchor :LINE_END
      brand-combobox
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 2, :anchor :LINE_START
      size-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 2,:anchor :LINE_END
      size-combobox
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 3, :anchor :LINE_START
      quantity-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 3,:anchor :LINE_END
      quantity-field
      :fill :HORIZONTAL, :insets (Insets. 2 2 2 2)
      :gridx 0, :gridy 4, :anchor :LINE_START
      price-label
      :fill :HORIZONTAL, :insets (Insets. 1 1 1 1)
      :gridx 1, :gridy 4,:anchor :LINE_END
      price-field
      :gridx 1, :gridy 5
      add-button
      :gridx 1, :gridy 6
      cancel-button
      :gridx 1, :gridy 7
      exit-button)))

;; jdialog
(defn inventory-dialog [data-ref]
  ; cleate dialog, labels and text fields
  (prn "inventory-dialog")
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

(defn dialog-string [owner id liquor brand size quantity price]
  (inventory-dialog (ref {:owner owner :id id :liquor liquor :brand brand
                      :size size :quantity quantity :price price})))

;; edit table mouse listener event
(.addMouseListener utl/test-table
                   (proxy [MouseAdapter] []
                     (mouseClicked [e]
                       (when (== (.getClickCount e) 1)
                         (let [coordinates (.getPoint e)
                               row-count (.rowAtPoint utl/test-table coordinates)
                               column-count (.columnAtPoint utl/test-table coordinates)]
                           (println "mouse...!!")
                           #_(let [id (:id(nth (db/list-admin)row-count))
                                 username (:username(nth (db/list-admin)row-count))
                                 password (:password(nth (db/list-admin)row-count))]
                             (dialog-string nil (str id) username password))
                           ;(flush)
                           )))))
;; admin frame
(defn exec-inventory-frame
  "Function exposes inventory frame"
  []
  (def inventory-frame
    (doto (JFrame. "INFINITE INVENTORY SYSTEM")
      ; set login panel to frame
      (.setContentPane inventory-panel)
      ; set size of frame
      (.setSize 800 300)
      ; make frame visible
      (.setVisible true)
      ; position frame at center of screen
      (.setLocationRelativeTo nil))))

(exec-inventory-frame)
