(ns monkey.aero
  (:require [aero.core :as ac]
            [buddy.core.keys.pem :as pem]
            [clojure.edn :as edn]
            [medley.core :as mc])
  (:import java.util.Base64
           [java.io PushbackReader StringReader]))

(defn- resolve-path [{:keys [resolver source]} path]
  ;; Copied from the aero source, which unfortunately does not expose this as a fn.
  ;; Would be better as a protocol imho.
  (if (map? resolver)
    (get resolver path)
    (resolver source path)))

(defmethod ac/reader 'file [opts _ arg]
  (let [p (resolve-path opts arg)]
    (slurp p)))

(defn ->b64
  "Converts the input string to base64"
  [x]
  (.encodeToString (Base64/getEncoder) (.getBytes x)))

(defn b64->
  "Decodes from base64, returns a byte array."
  [x]
  (.decode (Base64/getDecoder) x))

(defmethod ac/reader 'to-b64 [_ _ arg]
  (->b64 arg))

(defmethod ac/reader 'from-b64 [_ _ arg]
  (b64-> arg))

(defmethod ac/reader 'privkey [_ _ arg]
  (with-open [r (java.io.StringReader. arg)]
    (pem/read-privkey r nil)))

(defmethod ac/reader 'to-edn [_ _ arg]
  (pr-str arg))

(defmethod ac/reader 'from-edn [opts _ arg]
  (with-open [r (PushbackReader. (StringReader. arg))]
    (edn/read r)))

(defmethod ac/reader 'deep-merge [opts _ args]
  (apply mc/deep-merge args))

(defmulti ->str class)

(defmethod ->str java.lang.String [arg]
  arg)

(def byte-array-class (class (byte-array 0)))

(defmethod ->str byte-array-class [arg]
  (String. arg))

(defmethod ->str :default [arg]
  (str arg))

(defmethod ac/reader 'str [opts _ arg]
  (->str arg))

(defmethod ac/reader 'random [opts _ args]
  (nth args (rand-int (count args))))
