package au.com.wex.internationalpayments.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {

    public static BigDecimal convert(String s, int decimal) {
        BigDecimal b = new BigDecimal(s);
        return b.setScale(decimal, RoundingMode.CEILING);
    }

    public static BigDecimal multiplyAndScale(BigDecimal b1, BigDecimal b2, int scale) {
        BigDecimal b = b1.multiply(b2);
        b = b.setScale(scale, RoundingMode.CEILING);
        return b;
    }
}
