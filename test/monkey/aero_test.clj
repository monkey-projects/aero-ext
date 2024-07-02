(ns monkey.aero-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure.java.io :as io]
            [aero.core :as ac]
            [monkey.aero :as sut]))

(deftest file
  (testing "reads file using the resolver"
    (is (= {:some-file "This is a test file\n"}
           (ac/read-config (io/resource "file-test.edn") {:resolver ac/resource-resolver})))))

(deftest to-b64
  (testing "converts arg to base64"
    (is (= {:value "anVzdCB0ZXN0aW5n"}
           (ac/read-config (io/resource "to-b64-test.edn"))))))

(deftest from-b64
  (testing "converts arg from base64 to byte array"
    (is (= "just testing"
           (-> (ac/read-config (io/resource "from-b64-test.edn"))
               :value
               (String.))))))

(deftest privkey
  (testing "reads arg as pem private key"
    (is (instance? java.security.PrivateKey (-> (ac/read-config (io/resource "privkey-test.edn"))
                                                :private-key)))))

(deftest to-edn
  (testing "converts arg to edn"
    (is (= {:text "{:key \"value\"}"}
           (ac/read-config (io/resource "to-edn-test.edn"))))))

(deftest from-edn
  (testing "parses arg from edn"
    (is (= {:edn {:key "value"}}
           (ac/read-config (io/resource "from-edn-test.edn"))))))
