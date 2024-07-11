(ns monkey.aero-test
  (:require [clojure.test :refer [deftest testing is]]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [aero.core :as ac]
            [monkey.aero :as sut]))

(defn with-tmp-dir* [f]
  (let [dir (fs/create-temp-dir)]
    (try
      (f dir)
      (finally
        (fs/delete-tree dir)))))

(defmacro with-tmp-dir [dir & body]
  `(with-tmp-dir*
     (fn [d#]
       (let [~dir d#]
         ~@body))))

(defn read-test-config [s]
  (with-tmp-dir dir
    (let [f (fs/file (fs/path dir "test-config.edn"))]
      (spit f s)
      (ac/read-config f))))

(deftest file
  (testing "reads file using the resolver"
    (is (= {:some-file "This is a test file\n"}
           (ac/read-config (io/resource "file-test.edn") {:resolver ac/resource-resolver})))))

(deftest to-b64
  (testing "converts arg to base64"
    (is (= {:value "anVzdCB0ZXN0aW5n"}
           (read-test-config "{:value #to-b64 \"just testing\"}")))))

(deftest from-b64
  (testing "converts arg from base64 to byte array"
    (is (= "just testing"
           (-> (read-test-config "{:value #from-b64 \"anVzdCB0ZXN0aW5n\"}")
               :value
               (String.))))))

(deftest privkey
  (testing "reads arg as pem private key"
    (is (instance? java.security.PrivateKey (-> (ac/read-config (io/resource "privkey-test.edn"))
                                                :private-key)))))

(deftest to-edn
  (testing "converts arg to edn"
    (is (= {:text "{:key \"value\"}"}
           (read-test-config "{:text #to-edn {:key \"value\"}}")))))

(deftest from-edn
  (testing "parses arg from edn"
    (is (= {:edn {:key "value"}}
           (read-test-config "{:edn #from-edn \"{:key \\\"value\\\"}\"}")))))

(deftest deep-merge
  (testing "deep-merges maps"
    (is (= {:first {:second {:third "value"
                             :fourth "other value"}}}
           (ac/read-config (io/resource "deep-merge-test.edn"))))))

(deftest str-tag
  (testing "converts byte array arg to string"
    (is (= {:value "just testing"}
           (read-test-config "{:value #str #from-b64 \"anVzdCB0ZXN0aW5n\"}"))))

  (testing "converts number to string"
    (is (= {:value "100"}
           (read-test-config "{:value #str 100}"))))

  (testing "leaves string arg unchanged"
    (is (= {:value "test"}
           (read-test-config "{:value #str \"test\"}")))))

(deftest random
  (testing "selects item at random from arg"
    (let [items (->> (range 10)
                     (mapv (partial str "item-")))]
      (is (contains? (set items)
                     (-> (format "{:item #random %s}" (pr-str items))
                         (read-test-config)
                         :item))))))
