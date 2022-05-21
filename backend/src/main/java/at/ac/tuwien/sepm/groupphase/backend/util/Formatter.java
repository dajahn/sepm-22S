package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.enums.SeatType;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Formatter {
    public static String formatPrice(float price) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        return "€ " + nf.format(price);
    }

    public static String formatDate(LocalDate date) {
        return date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear();
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDate(dateTime.toLocalDate()) + " " + addLeadingZero(dateTime.getHour()) + ":" + addLeadingZero(dateTime.getMinute());
    }

    private static String addLeadingZero(int numb) {
        return (numb <= 9 ? "0" : "") + numb;
    }

    public static String formatSeatType(SeatType type) {
        return switch (type) {
            case NONE -> "Normal";
            case PREMIUM -> "Premium";
            case VIP -> "VIP";
        };
    }
}
