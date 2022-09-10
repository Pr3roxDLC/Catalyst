package com.krazzzzymonkey.catalyst.ellerton.japng.argb8888;

import com.krazzzzymonkey.catalyst.ellerton.japng.argb8888.Argb8888Bitmap;

/**
 * Handy methods to convert bitmaps to text.
 * This is useful because external tools can be written (see the japng/utils directory)
 * to read PNG files in the usual way and produce text versions. The unit tests can then
 * compare every pixel of the reference and test image and produce output in a way that
 * a human can track down exactly which pixels are problems.
 */
public class HexHelper {

//    public static String hexStringExtractColumn(String s, int x) {
//        String lines[] = s.split(System.getProperty("line.separator"));
//        StringBuilder sb = new StringBuilder();
//        for (String line : lines) {
//            String parts[] = line.split("\\s+");
//            sb.append(parts[x]);
//            sb.append("\n");
//        }
//        return sb.toString();
//    }

    public static String columnToRgbHex(Argb8888Bitmap bitmap, int x) {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < bitmap.height; y++) {
            int c = bitmap.array[y*bitmap.width+x];
            sb.append(String.format("%06X\n", c & 0x00ffffff));
            //colourToRgbHex(c, sb);
        }
        return sb.toString();
    }

    public static String columnsToRgbHex(Argb8888Bitmap bitmap) {
        return columnsToRgbHex(bitmap, 0, bitmap.width);
    }

    public static String columnsToRgbHex(Argb8888Bitmap bitmap, int i, int j) {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < bitmap.height; y++) {
            //int c = bitmap.array[y*bitmap.width+x];
            //sb.append(String.format("%06X\n", c & 0x00ffffff));
            for (int x=i; x<j; x++){
                int argb = bitmap.array[y * bitmap.width + x];
                sb.append(String.format("%06X ", argb & 0x00ffffff));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String columnToArgbHex(Argb8888Bitmap bitmap, int x) {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < bitmap.height; y++) {
            int argb = bitmap.array[y*bitmap.width+x];
            sb.append(String.format("%08X\n", argb));

//            int a=(argb&0xFF000000) >> 24 & 0xff;
//            int rgb=(argb&0x00FFFFFF);
//            sb.append(String.format("%02X-%06X\n", a, rgb));
        }
        return sb.toString();
    }


    public static String columnsToArgbHex(Argb8888Bitmap bitmap) {
        return columnsToArgbHex(bitmap, 0, bitmap.width);
    }

    public static String columnsToArgbHex(Argb8888Bitmap bitmap, int i, int j) {
        StringBuilder sb = new StringBuilder();
        for (int y=0; y < bitmap.height; y++) {
            for (int x=i; x<j; x++){

                int argb = bitmap.array[y * bitmap.width + x];
                sb.append(String.format("%08X ", argb));

                //            int a=(argb&0xFF000000) >> 24 & 0xff;
                //            int rgb=(argb&0x00FFFFFF);
                //            sb.append(String.format("%02X-%06X\n", a, rgb));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
