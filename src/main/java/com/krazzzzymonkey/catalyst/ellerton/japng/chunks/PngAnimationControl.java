package com.krazzzzymonkey.catalyst.ellerton.japng.chunks;

/**
 * A PngAnimationControl object contains data parsed from the ``acTL``
 * animation control chunk of an animated PNG file.
 */
public class PngAnimationControl {
    public final int numFrames;
    public final int numPlays;

    public PngAnimationControl(int numFrames, int numPlays) {
        this.numFrames = numFrames;
        this.numPlays = numPlays;
    }

    public boolean loopForever() {
        return 0 == numPlays;
    }

    @Override
    public String toString() {
        return "PngAnimationControl{" +
                "numFrames=" + numFrames +
                ", numPlays=" + numPlays +
                '}';
    }
}
