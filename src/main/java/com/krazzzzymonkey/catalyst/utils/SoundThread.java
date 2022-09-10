package com.krazzzzymonkey.catalyst.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class SoundThread implements Runnable {

    public static boolean isOpen = false;

    public File file = null;

    public SoundThread(File file) {
        this.file = file;
    }


    @Override
    public void run() {
        playClip(file);
        isOpen = false;
    }

    public static void playClip(File file) {

        try (final AudioInputStream in = getAudioInputStream(file)) {

            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

            try (final SourceDataLine line =
                     (SourceDataLine) AudioSystem.getLine(info)) {

                if (line != null) {
                    if (!isOpen) {
                        line.open(outFormat);
                        isOpen = true;
                        line.start();
                        stream(getAudioInputStream(outFormat, in), line);
                        line.drain();
                        line.stop();
                    }
                }
            }

        } catch (UnsupportedAudioFileException
            | LineUnavailableException
            | IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private static AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private static void stream(AudioInputStream in, SourceDataLine line)
        throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }


}
