;; namespace & libs
;; Namespace contains helper functions
(ns util.utils
  (:import (javax.swing JOptionPane))
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojure.tools.logging :as log]))

;; configuration-file path
(def conf "E:\\infinite\\infinite\\src\\config.properties")

(defn- load-config
  "Function loads configuration file"
  [filename]
  (with-open [r (io/reader filename)]
    (read (java.io.PushbackReader. r))))

(defn get-configuration
  "Function gets configuration"
  [key]
  ((load-config conf)key))

(defn convert-to-long
  "Function converts string to Long"
  [value]
  (try
    (Long/parseLong value)
    (catch NumberFormatException e
      (log/error "convert-to-long NFE => " (.getMessage e))
      (JOptionPane/showMessageDialog
        nil (str "Only numbers allowed, \"" value "\" is not a valid number!") "Input Problem"
        JOptionPane/ERROR_MESSAGE))
    (catch Exception e
      (log/error "convert-to-long E => " (.getMessage e))
      (JOptionPane/showMessageDialog
        nil "Problem with input!" "Input Problem"
        JOptionPane/ERROR_MESSAGE))))