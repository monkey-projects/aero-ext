(ns monkey.aero
  (:require [aero.core :as ac]
            [buddy.core.keys.pem :as pem])
  (:import java.util.Base64))

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

(defmethod ac/reader 'edn [_ _ arg]
  (pr-str arg))
