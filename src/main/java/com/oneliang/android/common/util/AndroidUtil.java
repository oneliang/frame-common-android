package com.oneliang.android.common.util;

public final class AndroidUtil {

    private AndroidUtil() {
    }

    /**
     * dip to pixel
     * @param density
     * @param dipValue
     * @return int
     */
    public static int dipToPixel(float density, float dipValue) {
        return (int) (dipValue * density + 0.5f);
    }

    /**
     * pixel to dip
     * @param density
     * @param pxValue
     * @return int
     */
    public static int pixelToDip(float density, float pixelValue) {
        return (int) (pixelValue / density + 0.5f);
    }
}
