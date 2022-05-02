package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class Address {

    @Column(nullable = false, length = 255)
    private String street;

    @Column(nullable = false, length = 31)
    private String zipCode;

    @Column(nullable = false, length = 127)
    private String city;

    @Column(nullable = false, length = 127)
    private String country;
}
