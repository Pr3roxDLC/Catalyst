package com.krazzzzymonkey.catalyst.ellerton.japng.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * A specific-length slice of some other InputStream.
 */
public class InputStreamSlice extends InputStream {
    protected final InputStream src;
    protected final int length;
    protected int position = 0;
    protected boolean atEof = false;

    public InputStreamSlice(InputStream src, int length) {
        this.src = src;
        this.length = length;
    }

    public int tell() {
        return position;
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (atEof || position >= length) {
            atEof = true;
            return -1;
        }
        int rv = src.read(b, 0, Math.min(b.length, length - position));
        if (rv > 0) {
            position += rv;
        }
        return rv;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (atEof || position >= length) {
            atEof = true;
            return -1;
        }
        int rv = src.read(b, off, Math.min(len, length - position));
        if (rv > 0) {
            position += rv;
        }
        return rv;
    }

    @Override
    public long skip(long n) throws IOException {
        if (atEof || position >= length) {
            atEof = true;
            return -1;
        }
        n = Math.min(available(), n); // calculate maximum skip
        n = src.skip(n); // attempt to skip that much
        position += n; // adjust position by correct skip
        return n;
    }

    @Override
    public int available() throws IOException {
        if (atEof || position >= length) {
            return 0;
        }
        return length - position;
    }

    @Override
    public int read() throws IOException {
        if (atEof || position >= length) {
            atEof = true;
            return -1;
        }

        int rv = src.read();
        if (rv < 0) {
            atEof = true;
        } else if (rv > 0) {
            this.position += rv;
        }
        return rv;
    }
}
