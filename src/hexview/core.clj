(ns hexview.core
  (:import [java.io File RandomAccessFile FileNotFoundException]))

;; Thanks go out to kumarshantanu and TimMc from #clojure for contributions of
;; code and ideas

(defn printable?
  "Returns logical true is the given value is not an ISO control character."
  [val]
  (not (Character/isISOControl val)))

(defn ascii?
  "Returns true if the given value represents a printable ASCII character."
  [val]
  (< 0x1f val 0x7f))

(defn ascii
  "Returns the ASCII character corresponding to the given value if it is a printable
ASCII character, or a period otherwise."
  [val]
  (let [modval (mod val 256)]
    (if (ascii? modval)
      (char modval)
      \.)))

(defn hex-dump-lines
  "Takes a sequence of numerical values and returns a seq of strings
  representing the hexadecimal data display portion of a hexdump. Only displays
  the least significant byte of each value."
  [s]
  (->> (map #(mod % 256) s)
       (map #(format "%02x" %))
       (partition-all 2)
       (interpose \space)
       flatten
       (apply str)
       (partition 40 40 (repeat \space))
       (map #(apply str %))))

(defn ascii-lines
  [s]
  (->> (map #(ascii %) s)
       (partition 16 16 (repeat \space))
       (map #(apply str %))))

(defn hexview-lines
  "Creates a sequence of lines representing a hexdump of the given argument.
Optionally supply byte offset (default: 0) and size (default: :all) arguments.
Can create hexdump from a collection of values, a java.io.File and a String
representing a path to a file."
  ([s]
     (hexview-lines s 0 :all))
  ([s offset]
     (hexview-lines s offset :all))
  ([s offset size]
     (cond
      (string? s) (let [f (File. s)]
                    (if (.exists f)
                      (hexview-lines f offset size)
                      (throw (FileNotFoundException. s))))
      (instance? java.io.File s) (with-open [raf (RandomAccessFile. s "r")]
                                   (let [fs (.length raf)
                                         bs (byte-array fs)]
                                     (.read raf bs 0 fs)
                                     (hexview-lines (seq bs) offset size)))
      (coll? s) (let [index (fn [s]
                              (map vector (range) s))
                      vals (->> s
                                index
                                (drop-while #(< (first %) offset))
                                (#(if (= size :all)
                                    %
                                    (take size %)))
                                (map second))
                      byte-offsets (map #(format "%08x: " %) (map #(* 16 %) (range)))
                      hex-data-lines (hex-dump-lines vals)
                      ascii-lines (ascii-lines vals)
                      parts-seq (map list byte-offsets hex-data-lines (repeat \space) ascii-lines (repeat \newline))]
                  (map #(apply str %) parts-seq))
      :else (throw (RuntimeException. "Can only hexdump a collection, a java.io.File or a String representing a path to a file.")))))

(defn hexview
  "Prints a hexdump of the given argument to *out*.  Optionally supply byte
offset (default: 0) and size (default: :all) arguments.  Can create hexdump from
a collection of values, a java.io.File and a String representing a path to a
file."
  ([s]
     (let [hexview-lines (hexview-lines s)
           hexview-str (apply str hexview-lines)]
       (println hexview-str)))
  ([s offset]
     (let [hexview-lines (hexview-lines s offset)
           hexview-str (apply str hexview-lines)]
       (println hexview-str)))
  ([s offset size]
     (let [hexview-lines (hexview-lines s offset size)
           hexview-str (apply str hexview-lines)]
       (println hexview-str))))