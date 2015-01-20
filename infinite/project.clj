(defproject infinite "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [propertea "1.2.3"]                        ;; reading from .properties
                 [clj-pdf "1.11.6"]                         ;; generate pdf
                 [org.clojure/java.jdbc "0.0.6"]            ;; jdbc
                 [mysql/mysql-connector-java "5.1.25"]      ;; mysql connector
                 [org.clojure/tools.logging "0.2.6"]        ;; loggging
                 [log4j "1.2.17"]                           ;; logging
                 [jfree/jfreechart "1.0.13"]                ;; data visualization
                 [jfree/jcommon "1.0.15"]                   ;; data visualization
                 [incanter "1.5.2"]])                       ;; data visualization
