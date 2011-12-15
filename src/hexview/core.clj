(ns hexview.core)

;; Thanks go out to kumarshantanu and TimMc from #clojure for contributions of
;; code and ideas

(defn printable-or-period
  "Returns the ASCII character corresponding to the given value if it is a printable
ASCII character, or a period otherwise."
  [val]
  (let [modval (mod val 256)
        printable? (fn [val]
                     (or (and (> val 0x1f) (< val 0x7f)) (and (> val 0x9f) (< val 0xff))))]
    (if (printable? modval)
      (char modval)
      \.)))

(defn hexstr-recursive
  [bytes]
  (loop [cnt 0
         mybytes (seq bytes)
         hexstr ""]
    (if (seq mybytes)
      (cond
       (= (mod (inc cnt) 24) 0) (recur (inc cnt) mybytes (str hexstr \newline))
       (= (mod (inc cnt) 12) 0) (recur (inc cnt) mybytes (str hexstr \space \space))
       (= (mod (inc cnt) 3) 0) (recur (inc cnt) mybytes (str hexstr \space))
       :else (recur (inc cnt) (rest mybytes) (str hexstr (format "%02X" (first mybytes)))))
      hexstr)))

(defn hexstr2
  [bytes]                                                                       
  (let [f #(format "%02X" %)                                                    
        h (map f bytes)
        s (interpose \space (partition-all 2 h))
        s2 (interpose \space (partition-all 8 s))
        s3 (interpose \newline (partition-all 4 s2))]
    (apply str (flatten s3))))

(defn hexstr3
  [bytes]
  (let [bytes (seq bytes)
        hexstrseq (for [sixteenbytes (partition 16 bytes)]
                    (str
                     (for [twobytes (partition 2 sixteenbytes)]
                       (apply #(format "%02X%02X " %) twobytes))
                     "\n"))]
    (apply str hexstrseq)))

(defn hexstr4
  [bytes]
  (let [heading (map #(format "%01X%01X" %1 %2) (range 1 17) (range 1 17))
        printable-ascii-chunks (partition 16 16 (repeat " ") (map #(printable-or-period %) bytes))
        hex (map #(format "%02X" %) bytes)
        s (interpose \space (partition-all 2 hex))
        s2 (interpose \space (partition-all 8 s))
        hexstr (apply str (flatten s2))
        hexchunks (partition 42 42 (repeat " ") hexstr)]
    (apply str (flatten (interleave hexchunks printable-ascii-chunks (repeat \newline))))))

(defn hex-dump-lines
  [s]
  (->> (map #(mod % 256) s)
       (map #(format "%02x" %))
       (partition-all 2)
       (interpose \space)
       (partition-all 8)
       (interpose \space)
       flatten
       (apply str)
       (partition 42 42 (repeat \space))
       (map #(apply str %))))

(defn ascii-lines
  [s]
  (->> (map #(printable-or-period %) s)
       (partition 16 16 (repeat \space))
       (map #(apply str %))))

(defn hexview-lines
  [s]
  (let [byte-offsets (map #(format "%08x: " %) (map #(* 16 %) (range)))
        hex-data-lines (hex-dump-lines s)
        ascii-lines (ascii-lines s)
        parts-seq (map list byte-offsets hex-data-lines ascii-lines (repeat \newline))]
    (map #(apply str %) parts-seq)))