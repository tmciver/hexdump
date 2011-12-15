(ns hexview.test.core
  (:use [hexview.core])
  (:use [clojure.test]))

(deftest test-printable?
  (is (every? false? (map #(printable? %) (range 0 0x20))))
  (is (every? true? (map #(printable? %) (range 0x20 0x7f))))
  (is (every? false? (map #(printable? %) (range 0x7f 0xa0))))
  (is (every? true? (map #(printable? %) (range 0xa0 0xff)))))