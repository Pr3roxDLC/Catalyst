package com.krazzzzymonkey.catalyst.ellerton.japng.reader;

import com.krazzzzymonkey.catalyst.ellerton.japng.error.PngException;

import java.io.IOException;

/**
 * All PngReader implementations need to read a specific single chunk and to return
 * a result of some form.
 */
public interface PngReader<ResultT> {
    boolean readChunk(PngSource source, int code, int dataLength) throws PngException, IOException;
    void finishedChunks(PngSource source) throws PngException, IOException;
    ResultT getResult();
}
