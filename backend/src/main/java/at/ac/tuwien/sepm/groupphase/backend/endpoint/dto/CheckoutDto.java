package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Data;

@Data
public class CheckoutDto {

    private String cardholder;
    private String cardnumber;
    private String exp;
    private String csc;
}
