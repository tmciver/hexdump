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
            "00000010: 0b1a 071d 1517 2f20 250c 192e 0d1b       ....../ %.....  \n"])))
  (let [ba (byte-array (map byte (range -128 128)))
        bb (java.nio.ByteBuffer/wrap ba)]
    (is (= (hexdump-lines bb)
           ["00000000: 8081 8283 8485 8687 8889 8a8b 8c8d 8e8f  ................\n"
            "00000010: 9091 9293 9495 9697 9899 9a9b 9c9d 9e9f  ................\n"
            "00000020: a0a1 a2a3 a4a5 a6a7 a8a9 aaab acad aeaf  ................\n"
            "00000030: b0b1 b2b3 b4b5 b6b7 b8b9 babb bcbd bebf  ................\n"
            "00000040: c0c1 c2c3 c4c5 c6c7 c8c9 cacb cccd cecf  ................\n"
            "00000050: d0d1 d2d3 d4d5 d6d7 d8d9 dadb dcdd dedf  ................\n"
            "00000060: e0e1 e2e3 e4e5 e6e7 e8e9 eaeb eced eeef  ................\n"
            "00000070: f0f1 f2f3 f4f5 f6f7 f8f9 fafb fcfd feff  ................\n"
            "00000080: 0001 0203 0405 0607 0809 0a0b 0c0d 0e0f  ................\n"
            "00000090: 1011 1213 1415 1617 1819 1a1b 1c1d 1e1f  ................\n"
            "000000a0: 2021 2223 2425 2627 2829 2a2b 2c2d 2e2f   !\"#$%&'()*+,-./\n"
            "000000b0: 3031 3233 3435 3637 3839 3a3b 3c3d 3e3f  0123456789:;<=>?\n"
            "000000c0: 4041 4243 4445 4647 4849 4a4b 4c4d 4e4f  @ABCDEFGHIJKLMNO\n"
            "000000d0: 5051 5253 5455 5657 5859 5a5b 5c5d 5e5f  PQRSTUVWXYZ[\\]^_\n"
            "000000e0: 6061 6263 6465 6667 6869 6a6b 6c6d 6e6f  `abcdefghijklmno\n"
            "000000f0: 7071 7273 7475 7677 7879 7a7b 7c7d 7e7f  pqrstuvwxyz{|}~.\n"]))))