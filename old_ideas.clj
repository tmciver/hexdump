;; This file contains remnants of earlier attempts at hexdump-related functions
;; that I didn't want leave stranded in the bowels of git history.


(defn extended-ascii
  "Returns the ASCII or extended ASCII character corresponding to the given value
if it is a printable character, or a period otherwise."
  [val]
  (let [modval (mod val 256)]
    (if (printable? modval)
      (char modval)
      \.)))

(defn hexstr-recursive
  "Recursive version of function that produces a seq of strings representing the
hexadecimal data display portion of a hexdump."
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
  "Another version of a function that produces a seq of strings representing the
hexadecimal data display portion of a hexdump. This versions sets up data
  conversion in a let."
  [bytes]                                                                       
  (let [f #(format "%02X" %)                                                    
        h (map f bytes)
        s (interpose \space (partition-all 2 h))
        s2 (interpose \space (partition-all 8 s))
        s3 (interpose \newline (partition-all 4 s2))]
    (apply str (flatten s3))))

(defn hexstr3
  "Another version of a function that produces a seq of strings representing the
hexadecimal data display portion of a hexdump. This versions sets up data using
for list comprehension."
  [bytes]
  (let [bytes (seq bytes)
        hexstrseq (for [sixteenbytes (partition 16 bytes)]
                    (str
                     (for [twobytes (partition 2 sixteenbytes)]
                       (apply #(format "%02X%02X " %) twobytes))
                     "\n"))]
    (apply str hexstrseq)))