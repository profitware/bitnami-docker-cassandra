(ns user
  (:require [environ.core :refer [env]]
            [clojure.java.jdbc :as jdbc]))

(def cassandra-db {:dbtype "c*:datastax"
                   :dbname "stresscql"
                   :classname "com.github.cassandra.jdbc.CassandraDriver"
                   :hosts (env :cassandra-host "cassandra")
                   :user (env :cassandra-user "cassandra")
                   :port 9042
                   :password (env :cassandra-password "cassandra")})

;; (jdbc/query cassandra-db ["select * from blogposts limit 2"])
