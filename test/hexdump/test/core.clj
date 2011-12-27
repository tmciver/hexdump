(ns hexdump.test.core
  (:use hexdump.core
        clojure.test))

(deftest test-ascii?
  (is (every? false? (map ascii? (range 0 0x20))))
  (is (every? true? (map ascii? (range 0x20 0x7f))))
  (is (every? false? (map ascii? (range 0x7f 0x100)))))

(deftest test-ascii
  (is (every? #(= \. %) (map ascii (filter #(not (ascii? %)) (range 256)))))
  (is (every? #(= (char %) (ascii %)) (filter ascii? (range 256)))))

(deftest test-hexdump-lines
  (let [test-data [41 8 9 0 28 20 35 4 17 38 42 5 40 19 14 30 6 1 33 48 43
                   11 26 7 29 21 23 47 32 37 12 25 46 13 27 16 15 34 22 44
                   39 10 36 2 31 45 24 18 49 3]
        hex-lines-all (hexdump-lines test-data)
        hex-lines-some (hexdump-lines test-data :offset 5 :size 30)]
    (is (= hex-lines-all
           ["00000000: 2908 0900 1c14 2304 1126 2a05 2813 0e1e  ).....#..&*.(...\n"
            "00000010: 0601 2130 2b0b 1a07 1d15 172f 2025 0c19  ..!0+....../ %..\n"
            "00000020: 2e0d 1b10 0f22 162c 270a 2402 1f2d 1812  .....\".,'.$..-..\n"
            "00000030: 3103                                     1.              \n"]))
    (is (= hex-lines-some
           ["00000000: 1423 0411 262a 0528 130e 1e06 0121 302b  .#..&*.(.....!0+\n"
            "00000010: 0b1a 071d 1517 2f20 250c 192e 0d1b       ....../ %.....  \n"]))))