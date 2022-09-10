package com.krazzzzymonkey.catalyst.ellerton.japng.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * I/O utility functions.
 *
 * Originally there was a dependency on Google guava commons IO but only a minimal part of that
 * (fantastic) library was needed.
 *
 * TODO: ideally, remove these.
 */
public class IoHelper {
    private static final int BUF_SIZE = 0x1000; // 4K

    /**
     * Copies all bytes from the input stream to the output stream.
     * Does not close or flush either stream.
     *
     * Note: this is from com.google.common.io.ByteStreams
     *
     * @param from the input stream to read from
     * @param to the output stream to write to
     * @return the number of bytes copied
     * @throws IOException if an I/O error occurs
     */
    public static long copy(InputStream from, OutputStream to)
            throws IOException {
//        checkNotNull(from);
//        checkNotNull(to);
        byte[] buf = new byte[BUF_SIZE];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }

    /**
     * Reads all bytes from an input stream into a byte array.
     * Does not close the stream.
     *
     * Note: this is taken directly from com.google.common.io.ByteStreams
     *
     * @param in the input stream to read from
     * @return a byte array containing all the bytes from the stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

}
