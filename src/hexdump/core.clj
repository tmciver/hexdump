(ns hexdump.core
  (:import [java.io File RandomAccessFile FileNotFoundException]
           [java.nio ByteBuffer]))

;; Thanks go out to kumarshantanu and TimMc from #clojure for contributions of
;; code and ideas

(defn ascii?
  "Returns true if the given value represents a printable ASCII character."
  [val]
  (< 0x1f val 0x7f))

(defn ascii
  "Returns the ASCII character corresponding to the low order byte of the given
value if it is a printable ASCII character, or a period otherwise."
  [val]
  (let [modval (mod val 256)]
    (if (ascii? modval)
      (char modval)
      \.)))

(defn hex-ascii-lines
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

(defn hexdump-lines
  "Creates a sequence of lines representing a hexdump of the given argument.
Optionally supply byte offset (:offset, default: 0) and size (:size, default:
:all) arguments.  Can create hexdump from a collection of values, a
java.nio.ByteBuffer, a java.io.File and a String representing a path to a file."
  [s & {:keys [offset size] :or {offset 0 size :all}}]
  (cond
   (string? s) (let [f (File. s)]
                 (if (.exists f)
                   (hexdump-lines f :offset offset :size size)
                   (throw (FileNotFoundException. s))))
   (instance? java.io.File s) (with-open [raf (RandomAccessFile. s "r")]
                                (let [fs (.length raf)
                                      bs (byte-array fs)]
                                  (.read raf bs 0 fs)
                                  (hexdump-lines (seq bs) :offset offset :size size)))
   (instance? java.nio.ByteBuffer s) (let [ba (.array s)]
                                       (hexdump-lines (seq ba) :offset 0 :size size))
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
                   hex-ascii-lines (hex-ascii-lines vals)
                   ascii-lines (ascii-lines vals)
                   parts-seq (map list byte-offsets hex-ascii-lines (repeat \space) ascii-lines (repeat \newline))]
               (map #(apply str %) parts-seq))
   :else (throw (RuntimeException. "Can only hexdump a collection, a java.io.File or a String representing a path to a file."))))

(defn hexdump
  "Prints a hexdump of the given argument to *out*.  Optionally supply
byte offset (:offset, default: 0) and size (:size, default: :all)
arguments.  Can create hexdump from a collection of values, a
java.nio.ByteBuffer, a java.io.File and a String representing a path
to a file."
  [s & {:keys [offset size] :or {offset 0 size :all}}]
  (let [hexdump-lines (hexdump-lines s :offset offset :size size)
        hexdump-str (apply str hexdump-lines)]
    (println hexdump-str)))