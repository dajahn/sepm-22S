package at.ac.tuwien.sepm.groupphase.backend.basetest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface CheckoutTestData {
    String CARDHOLDER = "Satoshi";
    String CARDNUMBER = "1234000012340000";
    String EXP = LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("MM/yy"));
    String CSC = "123";
}
