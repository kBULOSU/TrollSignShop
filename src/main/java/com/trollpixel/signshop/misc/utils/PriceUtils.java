package com.trollpixel.signshop.misc.utils;

public class PriceUtils {

    private static final String[] CHARS = {
            "K",
            "M",
            "B",
            "T",
            "Q",
            "QQ"
    };

    public static String toUserEnd(double count) {
        if (count < 1000D) {
            return String.format("%.2f", count);
        }

        int exp = (int) (Math.log(count) / Math.log(1000D));

        return String.format(
                "%.0f%s",
                count / Math.pow(1000D, exp),
                CHARS[exp - 1 >= CHARS.length ? CHARS.length - 1 : exp - 1]
        );
    }
}
