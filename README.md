# hexdump

A couple of functions for displaying numerical data as a hex dump (a la xxd).

## Usage

To hexdump the full contents of a file to \*out\*:

    (hexdump "/path/to/my-file")

or, use a `File` object:

    (hexdump (java.io.File. "/path/to/my-file"))

You can also hexdump a collection of values directly:

    (hexdump [67 108 111 106 117 114 101 32 82 111 99 107 115 33])

You can limit the data to be hexdumped via named arguments:

    (hexdump "/path/to/my-file" :offset 64 :size 128)

To obtain a lazy seq of hexdump lines to be consumed at your leisure, use the 'hexdump-lines' function (takes the same parameters as 'hexdump').

## Sample Output

The following code:

    (hexdump (range 128))

prints the following to \*out\*:

    00000000: 0001 0203 0405 0607 0809 0a0b 0c0d 0e0f  ................
    00000010: 1011 1213 1415 1617 1819 1a1b 1c1d 1e1f  ................
    00000020: 2021 2223 2425 2627 2829 2a2b 2c2d 2e2f   !"#$%&'()*+,-./
    00000030: 3031 3233 3435 3637 3839 3a3b 3c3d 3e3f  0123456789:;<=>?
    00000040: 4041 4243 4445 4647 4849 4a4b 4c4d 4e4f  @ABCDEFGHIJKLMNO
    00000050: 5051 5253 5455 5657 5859 5a5b 5c5d 5e5f  PQRSTUVWXYZ[\]^_
    00000060: 6061 6263 6465 6667 6869 6a6b 6c6d 6e6f  `abcdefghijklmno
    00000070: 7071 7273 7475 7677 7879 7a7b 7c7d 7e7f  pqrstuvwxyz{|}~.

The block on the left gives the offset (hexadecimal) of the first byte for that line.  The center block shows the hexadecimal representation of the data.  The block on the right shows the ASCII representation of the data with periods being used in place of non-printable characters.

## License

Distributed under the Eclipse Public License, the same as Clojure.