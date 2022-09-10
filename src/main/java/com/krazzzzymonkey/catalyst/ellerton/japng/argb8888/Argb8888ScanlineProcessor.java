package com.krazzzzymonkey.catalyst.ellerton.japng.argb8888;

import com.krazzzzymonkey.catalyst.ellerton.japng.chunks.PngHeader;
import com.krazzzzymonkey.catalyst.ellerton.japng.reader.BasicScanlineProcessor;

/**
 * Base implementation that transforms scanlines of source pixels into scanlines of
 * ARGB8888 (32-bit integer) pixels in a destination Argb8888Bitmap object.
 *
 * Note: I wonder if a better name is ScanlineConverter or PixelTransformer.
 *
 * @see Argb8888Processors
 */
public abstract class Argb8888ScanlineProcessor extends BasicScanlineProcessor {
    protected Argb8888Bitmap bitmap;
    protected Argb8888Palette palette;
    protected int y=0;

//    public Argb8888ScanlineProcessor(PngHeader header) {
//        this(header.bytesPerRow, new Argb8888Bitmap(header.width, header.height));
//    }

    public Argb8888ScanlineProcessor(int bytesPerScanline, Argb8888Bitmap bitmap) {
        super(bytesPerScanline);
        this.bitmap = bitmap;
    }

//    public Argb8888ScanlineProcessor(PngHeader header, PngScanlineBuffer scanlineReader, PngFrameControl currentFrame) {
//        super(header, scanlineReader, currentFrame);
//        bitmap = new Argb8888Bitmap(this.header.width, this.header.height);
//    }

    public Argb8888Bitmap getBitmap() {
        return bitmap;
    }

    public Argb8888Palette getPalette() {
        return palette;
    }

    public void setPalette(Argb8888Palette palette) {
        this.palette = palette;
    }

    /**
     * When processing a sequence of frames a caller may want to write to a completely
     * new bitmap for every frame or re-use the bytes in an existing bitmap.
     *
     * @param header of image to use as basis for calculating image size.
     *
     * @return processor that will write to a new bitmap.
     */
    public Argb8888ScanlineProcessor cloneWithNewBitmap(PngHeader header) {
        return clone(header.bytesPerRow, new Argb8888Bitmap(header.width, header.height));
    }

    /**
     * When processing a sequence of frames a caller may want to write to a completely
     * new bitmap for every frame or re-use the bytes in an existing bitmap.
     *
     * @param header of image to use as basis for calculating image size.
     *
     * @return processor that will write to the same bitmap as the current processor.
     */
    public Argb8888ScanlineProcessor cloneWithSharedBitmap(PngHeader header) {
        return clone(header.bytesPerRow, bitmap.makeView(header.width, header.height));
    }

//    public Argb8888ScanlineProcessor clone(int bytesPerRow, Argb8888Bitmap bitmap) {
//        throw new IllegalStateException("TODO: override");
//    }
    abstract Argb8888ScanlineProcessor clone(int bytesPerRow, Argb8888Bitmap bitmap);
}
