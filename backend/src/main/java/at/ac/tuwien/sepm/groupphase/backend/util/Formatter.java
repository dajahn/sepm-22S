package at.ac.tuwien.sepm.groupphase.backend.util;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class Formatter {
    public static String formatPrice(float price) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        return "€ " + nf.format(price);
    }
}
